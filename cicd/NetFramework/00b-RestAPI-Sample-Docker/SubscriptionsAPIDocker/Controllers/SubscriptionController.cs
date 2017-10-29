using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using SubscriptionComponent;
using ModelComponent;

namespace SubscriptionsAPI.Controllers
{
    [Route("api/subscriptions")]
    public class SubscriptionController : Controller
    {
        // GET api/subscriptions
        [HttpGet]
        public IEnumerable<string> Get()
        {
            IMemberManager _manager = new MemberManager();
            List<string> members = new List<string>();
            foreach (Member member in _manager.GetMembers())
            {
                members.Add(String.Format("{0} : {1} {2}", member.MemberID, member.FirstName, member.SecondName));
            }
            return members;
        }

        // GET api/subscriptions/5
        [HttpGet("{id}")]
        public string Get(int id)
        {
            IMemberManager manager = new MemberManager();
            Subscription target = new Subscription(manager);
            Member member = manager.GetMember(id);
            return String.Format("{0} : {1} {2}., Total Subscription Cost {3} euros", member.MemberID, member.FirstName, member.SecondName, target.CalculateSubscriptionCost(id));
        }

        // POST api/subscriptions
        [HttpPost]
        public void Post([FromBody]string value)
        {
        }

        // PUT api/subscriptions/5
        [HttpPut("{id}")]
        public void Put(int id, [FromBody]string value)
        {
        }

        // DELETE api/values/5
        [HttpDelete("{id}")]
        public void Delete(int id)
        {
        }
    }
}
