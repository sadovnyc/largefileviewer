package orario.unittest;

import orario.core.OrarioInterval;
import junit.framework.TestCase;

public class OrarioIntervalTest extends TestCase {

	/*
	 * Test method for 'orario.core.OrarioInterval.geDurationInMinutes()'
	 */
	public void testGeDurationInMinutes()
	{
		OrarioInterval test = new OrarioInterval(0,20);
		assertEquals(20, test.geDurationInMinutes());
	}

	/*
	 * Test method for 'orario.core.OrarioInterval.getActivityName()'
	 */
	public void testGetActivityName()
	{
		OrarioInterval test = new OrarioInterval(0,20);
		test.setActivity("activity-name", "activity-type");
		assertEquals("activity-name", test.getActivityName());
	}

	/*
	 * Test method for 'orario.core.OrarioInterval.getActivityType()'
	 */
	public void testGetActivityType()
	{
		OrarioInterval test = new OrarioInterval(0,20);
		test.setActivity("activity-name", "activity-type");
		assertEquals("activity-type", test.getActivityType());
	}

	/*
	 * Test method for 'orario.core.OrarioInterval.equals(Object)'
	 */
	public void testEqualsObject()
	{
		OrarioInterval test1 = new OrarioInterval(0,20);
		test1.setActivity("activity-name", "activity-type");
		assertEquals("activity-name", "activity-type");
	}

}
