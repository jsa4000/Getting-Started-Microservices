using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using ModelComponent;
using SubscriptionComponent;
using NUnit.Framework;
using Moq;

namespace SubscriptionComponent.Test
{

    [TestFixture]
    class SubscriptionTest
    {

        private Subscription _target;
        private Mock<IMemberManager> _mock;
        private Member _member;

        [SetUp]
        public void Init()
        {
            _mock = new Mock<IMemberManager>();
            _target = new Subscription(_mock.Object);
            _member = new Member()
            {
                MemberID = 1,
                FirstName = "Javier",
                SecondName = "Santos",
                Age = 25,
                MaximunBooks = 4
            };
        }

        [TearDown]
        public void Cleanup()
        {
            // Dispose all the members created
        }

        [Test]
        public void ShouldComputeSubscriptionCostMember()
        {
            // Setup the Mocker to return the Member previously created
            _mock.Setup(x => x.GetMember(It.IsAny<int>())).Returns(_member);

           Assert.AreEqual(_target.CalculateSubscriptionCost(1), 70);
        }
    }

    
}
