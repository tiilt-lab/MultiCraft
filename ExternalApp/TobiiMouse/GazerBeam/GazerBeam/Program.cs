using System;
using System.Diagnostics;
using System.Net.Mime;
using System.Runtime.InteropServices;
using System.Windows.Forms;
using Tobii.Interaction;

namespace Interaction_Streams_101
{
    public class Program
    {
        // Global Variables
        private static Host _host;
        private static bool _usingDwell;

        // Windows API helper functions
        [DllImport("user32.dll")]
        public static extern bool GetCursorPos(out Point position);

        [DllImport("user32.dll")]
        private static extern int SetCursorPos(int x, int y);

        [DllImport("user32.dll", SetLastError = true)]
        private static extern IntPtr FindWindow(string className, string windowName);

        [DllImport("user32.dll")]
        private static extern bool GetWindowRect(IntPtr windowPtr, out Rectangle rect);

        [DllImport("user32.dll")]
        private static extern bool ScreenToClient(IntPtr windowPtr, ref Point windowPos);

        [DllImport("user32.dll")]
        public static extern short GetKeyState(int vKey);

        public static void Main(string[] args)
        {
            // Everything starts with initializing Host, which manages connection to the 
            // Tobii Engine and provides all the Tobii Core SDK functionality.
            // NOTE: Make sure that Tobii.EyeX.exe is running.
            _host = new Host();
            var collect = new Stopwatch();
            var dwell = new Stopwatch();
            bool moved;

            if (args.Length > 0 && args[0].Equals("-d"))
            {
                // Console.WriteLine("using dwell");
                _usingDwell = true;
            }

            // Get the currently running MultiCraft window. If no window found, end the program.
            var minecraftWindow = FindWindow(null, "Minecraft 1.9");
            if (!GetWindowRect(minecraftWindow, out var rect)) return;
            var windowWidth = rect.Right - rect.Left;
            var windowHeight = rect.Bottom - rect.Top;

            // 2. Create stream. 
            // Console.WriteLine("starting mirror...");
            var gazePointDataStream = _host.Streams.CreateGazePointDataStream();
            collect.Start();

            // 3. Get the gaze data.  
            gazePointDataStream.GazePoint((x, y, _) =>
            {
                var eyePos = new Point((int)x, (int)y);

                if (_usingDwell && dwell.IsRunning && dwell.Elapsed > TimeSpan.FromSeconds(3))
                {
                    // Console.WriteLine("dwell completed.");
                    EndHostConnection();
                }

                if (collect.Elapsed > TimeSpan.FromMilliseconds(16))
                {
                    if (GetCursorPos(out var cursorPos) && ScreenToClient(minecraftWindow, ref eyePos))
                    {
                        moved = MoveCursor(eyePos, cursorPos, windowWidth, windowHeight);
                        if (!moved && _usingDwell && !dwell.IsRunning)
                        {
                            // Console.WriteLine("dwell initiated.");
                            dwell.Start();
                        }
                        else if (moved && dwell.IsRunning)
                        {
                            // Console.WriteLine("dwell reset.");
                            dwell.Reset();
                        }
                    }
                    collect.Reset();
                    collect.Start();
                }
            });

            // Console.WriteLine("press . to stop");
            while (GetKeyState((int) Keys.OemPeriod) == 0)
            {
                // 4. Wait for user to press . key to end.
            }
            EndHostConnection();

        }

        private static bool MoveCursor(Point eyePos, Point cursorPos,  int windowWidth, int windowHeight)
        {
            var displaceX = eyePos.X - (windowWidth / 2);
            var displaceY = eyePos.Y - (windowHeight / 2);

            // bounding boxes (adjust this for more or less sensitivity for dwell)
            if (Math.Abs(displaceX) < 75) displaceX = 0;
            if (displaceY < 50 && displaceY > -200) displaceY = 0;

            if (displaceX == 0 && displaceY == 0)
                return false;

            // vertical sensitivity adjustment
            if (displaceY < 0) displaceY /= 2;

            // Console.WriteLine("moving x={0}, y={1}", displaceX, displaceY);
            SetCursorPos(cursorPos.X + displaceX / 4, cursorPos.Y + displaceY / 3);
            return true;
        }

        private static void EndHostConnection()
        {
            // 5. Close connection to the Tobii Engine before exit.
            // Console.WriteLine("stopping mirror...");
            _host.DisableConnection();
            Environment.Exit(0);
        }

        [StructLayout(LayoutKind.Sequential)]
        public struct Point
        {
            public int X;           // x position of cursor
            public int Y;           // y position of cursor

            public Point (int x, int y)
            {
                X = x;
                Y = y;
            }
        }

        [StructLayout(LayoutKind.Sequential)]
        public struct Rectangle
        {
            public int Left;        // x position of upper-left corner
            public int Top;         // y position of upper-left corner
            public int Right;       // x position of lower-right corner
            public int Bottom;      // y position of lower-right corner
        }
    }
}
