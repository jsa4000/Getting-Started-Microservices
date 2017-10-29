using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace StatisticsComponent
{

    /// <summary>
    /// 
    /// This class provides function for simple Operations suh as:
    /// 
    /// - Add
    /// - Mul
    /// 
    /// </summary>
    public class Common
    {

        /// <summary>
        ///  It computes the sum between two numbers
        /// </summary>
        /// <param name="num1">First Argument</param>
        /// <param name="num2">Second Argument</param>
        /// <returns></returns>
        public static int Add(int num1, int num2)
        {
            return num1 + num2;
        }

        /// <summary>
        /// 
        /// </summary>
        /// <param name="num1">First Argument</param>
        /// <param name="num2">Second Argument</param>
        /// <returns></returns>
        public static int Mul(int num1, int num2)
        {
            return num1 * num2;
        }
    }
  
}
