using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ModelComponent
{

    /// <summary>
    /// Memeber Manage implementation
    /// </summary>
    public class MemberManager : IMemberManager
    {
        private Dictionary<int, Member> _members = new Dictionary<int, Member>();

        /// <summary>
        /// DefaultConstructor.
        /// 
        /// It creates default memebers
        /// 
        /// </summary>
        public MemberManager()
        {
            this.CreateDefaultDatebase();
        }

        private void  CreateDefaultDatebase()
        {
            _members.Add(1, new Member() { MemberID = 1, FirstName = "Javier", SecondName = "Santos", Age = 34, MaximunBooks = 4 });
            _members.Add(2, new Member() { MemberID = 2, FirstName = "Manuel", SecondName = "Salva", Age = 35, MaximunBooks = 5 });
            _members.Add(3, new Member() { MemberID = 3, FirstName = "Alba", SecondName = "Vasco", Age = 32, MaximunBooks = 6 });
        }

        /// <summary>
        /// Function that Get a Member for a given Id
        /// </summary>
        /// <param name="memberID"></param>
        /// <returns></returns>
        public Member GetMember(int memberID)
        {
            //throw new NotImplementedException();
            return _members[memberID];
        }


        /// <summary>
        /// Get All the memebers in Database
        /// </summary>
        /// <param name="memberID"></param>
        /// <returns></returns>
        public IEnumerable<Member> GetMembers()
        {
            //throw new NotImplementedException();
            return _members.Values.ToList();
        }
    }
}
