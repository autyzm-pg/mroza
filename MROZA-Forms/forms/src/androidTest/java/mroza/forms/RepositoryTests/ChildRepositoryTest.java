/*
 * MROZA - supporting system of behavioral therapy of people with autism
 *     Copyright (C) 2015-2016 autyzm-pg
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package mroza.forms.RepositoryTests;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import database.Child;
import junit.framework.Assert;
import repositories.ChildRepository;
import mroza.forms.ChooseProgramActivity;
import mroza.forms.TestUtils.CompareUtils;
import mroza.forms.TestUtils.TestUtils;

import java.util.List;


public class ChildRepositoryTest extends ActivityInstrumentationTestCase2<ChooseProgramActivity> {


    private Context targetContext;
    private Child expectedChild;
    private int expectedNumberOfChildren;

    public ChildRepositoryTest() {
        super(ChooseProgramActivity.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
        targetContext = getInstrumentation().getTargetContext();
        TestUtils.cleanUpDatabase(targetContext);
        TestUtils.setUpSyncDateLaterThenTestExecute(targetContext);
        expectedNumberOfChildren = 3;
        List<Child> children = TestUtils.setUpChildren(targetContext, expectedNumberOfChildren, "CHILD_NUMBER_");
        expectedChild = children.get(0);
    }

    public void testChildRepositoryGetChildById() {

        Child foundChild = ChildRepository.getChildById(targetContext, expectedChild.getId());
        boolean compareResult = CompareUtils.areTwoChildsTheSame(foundChild, expectedChild);
        Assert.assertTrue("Children should be the same", compareResult);

    }

    public void testChildRepositoryGetAllChildren() {

        List<Child> foundChildren = ChildRepository.getChildrenList(targetContext);
        Assert.assertEquals("Children list should be equal", expectedNumberOfChildren, foundChildren.size());

    }

    public void testChildRepositoryGetSearchedChild() {

        List<Child> fundChildren = ChildRepository.getChildrenBySearch(targetContext, "CHILD_NUMBER_0" );
        Child foundChild = fundChildren.get(0);
        boolean compareResult = CompareUtils.areTwoChildsTheSame(foundChild, expectedChild);
        Assert.assertEquals("Searched child should be the same", true, compareResult);

    }
}