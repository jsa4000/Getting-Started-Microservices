using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ModelComponent
{

    /// <summary>
    /// Interface with the basic functionality of a Repository dealing with members.
    /// </summary>
    public interface IMemberManager
    {
        /// <summary>
        /// 
        /// Function that Get the instance of member for a given ID.
        /// 
        /// </summary>
        /// <param name="memberID">Memeber Id associated with the member Class</param>
        /// <returns></returns>
        Member GetMember(int memberID);

        /// <summary>
        /// Get all the Memeber in database. 
        /// 
        /// Nope: paganation has not been implemeneted.
        /// </summary>
        /// <returns></returns>
        IEnumerable<Member> GetMembers();
    }
}
