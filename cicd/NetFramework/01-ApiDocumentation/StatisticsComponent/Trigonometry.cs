using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace StatisticsComponent
{

    /// <summary>
    /// 
    /// This class provides function for Trigonometry operations suh as:
    /// 
    /// - Sin
    /// - Cos
    /// 
    /// </summary>
    public class Trigonometry
    {

        /// <summary>
        /// 
        /// Function to get the Sin of given angle in degrees
        /// 
        /// </summary>
        /// <param name="radians">Radians Argument</param>
        /// <returns></returns>
        public static double Sin(double degree)
        {
            return Math.Sin(Helpers.DegreeToRadian(degree));
        }

        /// <summary>
        /// 
        /// Function to get the Cosaine of a given angle in degrees
        /// 
        /// </summary>
        /// <param name="radians">Radians Argument</param>
        /// <returns></returns>
        public static double Cos(double degree)
        {
            return Math.Cos(Helpers.DegreeToRadian(degree));
        }

    }
}
