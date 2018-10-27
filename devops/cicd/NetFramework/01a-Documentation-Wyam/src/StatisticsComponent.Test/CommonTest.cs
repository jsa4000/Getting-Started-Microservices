using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using NUnit.Framework;
using StatisticsComponent;

namespace StatisticsComponent.Test
{

    [TestFixture]
    public class CommonTest
    {

        [Test]
        public void ShouldAddTwoNumbers()
        {
            Assert.AreEqual(Common.Add(3, 2), 5);
        }

        [Test]
         public void ShuouldMulTwoNumbers()
        {
            Assert.AreEqual(Common.Mul(3, 2), 6);
        }

    }
}
