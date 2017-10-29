using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ModelComponent
{

    /// <summary>
    /// 
    /// This is the class that defines the structure of a Member.
    /// 
    /// It basicalle contains properties such as:
    /// 
    ///  - ID
    ///  - Name: First and Secodn Name
    ///  - MaximunBooks that can handle
    ///  . Age
    /// 
    /// </summary>
    public class Member
    {
       /// <summary>
        /// MemberID Properyy
        /// </summary>
        public int MemberID { get; set; }

        /// <summary>
        /// FirstName Properyy
        /// </summary>
        public string FirstName { get; set; }

        /// <summary>
        /// SecondName Properyy
        /// </summary>
        public string SecondName { get; set; }

        /// <summary>
        /// MaximunBooks Properyy
        /// </summary>
        public int MaximunBooks { get; set; }

        /// <summary>
        /// Age Properyy
        /// </summary>
        public int Age { get; set; }

    }
}
