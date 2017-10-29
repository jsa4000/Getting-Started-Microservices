using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using ModelComponent;
using StatisticsComponent;

namespace SubscriptionComponent
{

    /// <summary>
    /// 
    /// Class that provides functionality for member's subscriptions
    /// 
    /// </summary>
    public class Subscription
    {

        /// <summary>
        /// Member Private variable Readonly
        /// </summary>
        private readonly IMemberManager _memberManager;

        /// <summary>
        /// Dependency injection for the Logic Component
        /// </summary>
        /// <param name="memberManager"></param>
        public Subscription(IMemberManager memberManager)
        {
            this._memberManager = memberManager;
        }

        /// <summary>
        /// 
        /// Method that compute the cost of a Member subscription based on the maximun books that can Handle.
        /// 
        /// </summary>
        /// <param name="memberID"></param>
        /// <returns></returns>
        public double CalculateSubscriptionCost(int memberID)
        {
            double membershipCost = 0;
            Member member = _memberManager.GetMember(memberID);
            membershipCost = Common.Mul(Common.Add(10, member.MaximunBooks), 5);
            return membershipCost;
        }
    }
}
