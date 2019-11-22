using System;
using System.Diagnostics;
using System.IO;
using System.Runtime.InteropServices;
using System.Threading;
using System.Windows.Forms;
using Tobii.Interaction;
using Tobii.Interaction.Framework;

namespace Interaction_Streams_101
{
    public class Program
    {
        [DllImport("user32.dll")]
        public static extern bool GetCursorPos(out POINT pos);

        [DllImport("user32.dll")]
        private static extern int SetCursorPos(int x, int y);

        public static void Main(string[] args)
        {
            // Everything starts with initializing Host, which manages connection to the 
            // Tobii Engine and provides all the Tobii Core SDK functionality.
            // NOTE: Make sure that Tobii.EyeX.exe is running
            var host = new Host();

            // 2. Create stream. 
            var gazePointDataStream = host.Streams.CreateGazePointDataStream();

            // 3. Get the gaze data.   
            Console.WriteLine("starting mirror...");

            int screenH = Screen.PrimaryScreen.Bounds.Height;
            int screenW = Screen.PrimaryScreen.Bounds.Width;

            Stopwatch d = new Stopwatch();
            d.Start();

            gazePointDataStream.GazePoint((x, y, _) =>
            {   
                if (d.Elapsed > TimeSpan.FromMilliseconds(15))
                {
                    POINT pos;
                    if (GetCursorPos(out pos))
                    {
                        int dispX = (int) x - screenW / 2;
                        if (Math.Abs(dispX) < 100) dispX = 0;
                        int dispY = (int) y - screenH / 2;
                        if (Math.Abs(dispY) < 100) dispY = 0;

                        SetCursorPos(pos.X + dispX, pos.Y + dispY);
                        Console.WriteLine("moving x={0}, y={1}", dispX, dispY);
                    }

                    d.Reset();
                    d.Start();
                }
            });

            //4. Close connection to the Tobii Engine before exit.
            Thread.Sleep(10000);
            Console.WriteLine("stopping mirror...");
            host.DisableConnection();
        }

        [StructLayout(LayoutKind.Sequential)]
        public struct POINT
        {
            public int X;
            public int Y;

            public POINT(int x, int y)
            {
                this.X = x;
                this.Y = y;
            }
        }
    }
}
