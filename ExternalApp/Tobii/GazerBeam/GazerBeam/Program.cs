using System;
using Tobii.Interaction;

using ReadWriteCsv;

namespace Interaction_Streams_101
{
    /// <summary>
    /// The data streams provide nicely filtered eye-gaze data from the eye tracker 
    /// transformed to a convenient coordinate system. The point on the screen where 
    /// your eyes are looking (gaze point), and the points on the screen where your 
    /// eyes linger to focus on something (fixations) are given as pixel coordinates 
    /// on the screen. The positions of your eyeballs (eye positions) are given in 
    /// space coordinates in millimeters relative to the center of the screen.
    /// 
    /// Let's see how is simple to find out where are you looking at the screen
    /// using GazePoint data stream, accessible from Streams property of Host instance.
    ///
    /// This is a modified version of Program.cs that also utilizes the eye-gaze
    /// data from the Tobii eye tracker to track eye movement in real time.
    /// Instead of writing to a .csv file, though, we will be trying to utilize
    /// the incoming GazePoint data to simulate moving the mouse around the screen
    /// wherever your eyes are looking.
    /// </summary>
    public class Program
    {
        public static void Main(string[] args)
        {
            // Everything starts with initializing Host, which manages connection to the 
            // Tobii Engine and provides all the Tobii Core SDK functionality.
            // NOTE: Make sure that Tobii.EyeX.exe is running
            var host = new Host();

            // 2. Create stream. 
            var gazePointDataStream = host.Streams.CreateGazePointDataStream();

            // Give the option to run live tracking instead of writing to csv
            Console.WriteLine("csv/track?");
            string mode = Console.ReadLine();

            // 3. Get the gaze data!
            if (mode.ToLower().Equals("csv"))
            {
                using (CsvFileWriter writer = new CsvFileWriter("TobiiData.csv"))
                {
                    gazePointDataStream.GazePoint((x, y, ts) =>
                    {
                        CsvRow row = new CsvRow();
                        row.Add(String.Format("{0},{1},{2}", ts, x, y));
                        writer.WriteRow(row);
                    });

                    Console.WriteLine("writing gaze data to TobiiData.csv...");
                    Console.WriteLine("press any key to end...");
                    Console.ReadKey();
                }
            }
            else if (mode.ToLower().Equals("track"))
            {
                
            }


            //4. Close the connection to the Tobii Engine before exit.
            host.DisableConnection();
        }
    }
}
