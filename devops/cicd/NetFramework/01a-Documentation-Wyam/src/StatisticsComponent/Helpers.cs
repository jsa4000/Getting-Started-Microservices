using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace StatisticsComponent
{

    /// <summary>
    /// 
    /// This class provides Helpers to perform some transformations with the data.
    /// 
    /// 
    /// </summary>
    public class Helpers
    {
        /// <summary>
        /// This function transforms degree to radians
        /// </summary>
        /// <param name="angle">Angle to transform into radians</param>
        /// <returns></returns>
        public static double DegreeToRadian(double angle)
        {
            return Math.PI * angle / 180.0;
        }

        /// <summary>
        /// This function transforms radians to degree
        /// </summary>
        /// <param name="angle">Angle to transform into degree</param>
        /// <returns></returns>
        public static double RadianToDegree(double angle)
        {
            return angle * (180.0 / Math.PI);
        }

    }
}
