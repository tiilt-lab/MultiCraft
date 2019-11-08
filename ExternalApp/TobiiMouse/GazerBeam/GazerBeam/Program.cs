using System;
using System.IO.Compression;
using System.Runtime.InteropServices;
using System.Windows.Input;
using Tobii.Interaction;
using Tobii.Interaction.Framework;

namespace Interaction_Streams_101
{
    public class Program
    {
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
            double updateWindow = 30;
            double prevUpdate = 0;
            Console.WriteLine("starting mirror...");
            
            gazePointDataStream.GazePoint((x, y, ts) => {
                Console.WriteLine("({0}, {1})", x, y);
                if (ts - prevUpdate > updateWindow)
                {
                    SetCursorPos((int)x, (int)y);
                    prevUpdate = ts;
                }
            });

            Console.WriteLine("press any key to quit...");
            Console.ReadKey();

            //4. Close connection to the Tobii Engine before exit.
            Console.WriteLine("stopping mirror...");
            host.DisableConnection();
        }
    }
}
