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
    public class TrigonometryTest
    {

        [Test]
        public void ShouldComputeSinInRadians()
        {
            Assert.AreEqual(Trigonometry.Sin(90), 1);
        }

        [Test]
         public void ShouldComputeCosInRadians()
        {
            Assert.AreEqual(Trigonometry.Cos(0), 1);
        }

    }
}
