package com.violenthoboenterprises.blistful;


import android.os.SystemClock;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.violenthoboenterprises.blistful.model.Reminder;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withChild;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.anything;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule =
            new ActivityTestRule<>(MainActivity.class, false, true);

    private MainActivity mActivity = null;

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void testFab(){
        onView(withId(R.id.fab)).perform(click());
        ViewInteraction textView = onView(withId(R.id.taskNameEditText));
        textView.check(matches(withText("")));
        onView(withId(R.id.taskNameEditText)).perform(typeText("test"));
        //clicking done on soft keyboard
        onView(withId(R.id.taskNameEditText)).perform(pressImeActionButton());
        onView(withId(R.id.taskNameEditText)).check(matches(withText("")));
    }

    @Test
    public void testChangeToReminder(){
        //switching to the reminder activity of the first item
        onView(withId(R.id.recyclerView)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, MyViewAction.clickChildViewWithId(R.id.alarm)));
        ViewInteraction cancelInit = onView(withId(R.id.cancelRepeat));
        ViewInteraction dailyInit = onView(withId(R.id.dailyLight));
        ViewInteraction weeklyInit = onView(withId(R.id.weeklyLight));
        ViewInteraction monthlyInit = onView(withId(R.id.monthlyLight));
        ViewInteraction cancelSecondary = onView(withId(R.id.cancelRepeatLight));
        ViewInteraction dailySecondary = onView(withId(R.id.daily));
        ViewInteraction weeklySecondary = onView(withId(R.id.weekly));
        ViewInteraction monthlySecondary = onView(withId(R.id.monthly));
        //clicking all the repeat buttons and checking that the correct images are displayed/hidden
        cancelInit.check(matches(isDisplayed()));
        dailyInit.check(matches(isDisplayed()));
        weeklyInit.check(matches(isDisplayed()));
        monthlyInit.check(matches(isDisplayed()));
        dailyInit.perform(click());
        cancelSecondary.check(matches(isDisplayed()));
        dailySecondary.check(matches(isDisplayed()));
        weeklyInit.perform(click());
        dailyInit.check(matches(isDisplayed()));
        weeklySecondary.check(matches(isDisplayed()));
        monthlyInit.perform(click());
        weeklyInit.check(matches(isDisplayed()));
        monthlySecondary.check(matches(isDisplayed()));
        cancelSecondary.perform(click());
        monthlyInit.check(matches(isDisplayed()));
        cancelInit.check(matches(isDisplayed()));
        Espresso.pressBack();
    }

    @Test
    public void testChangeToSubtasks(){
        //switching to the subtasks activity of the first item
        onView(withId(R.id.recyclerView)).perform(
                RecyclerViewActions.actionOnItemAtPosition
                        (0, MyViewAction.clickChildViewWithId(R.id.subTasks)));
        //check there are no subtasks
        onView(withId(R.id.subTasksRecyclerView))
                .check(new RecyclerViewItemCountAssertion(0));
        onView(withId(R.id.checklistEditText)).check(matches(withText("")));
        onView(withId(R.id.checklistEditText)).perform(typeText("test"));
        SystemClock.sleep(2000);
        //clicking done on soft keyboard
        onView(withId(R.id.checklistEditText)).perform(pressImeActionButton());
        onView(withId(R.id.checklistEditText)).check(matches(withText("")));
        onView(withId(R.id.subTasksRecyclerView))
                .check(new RecyclerViewItemCountAssertion(1));
        Espresso.pressBack();
        Espresso.pressBack();
    }

    @Test
    public void testChangeToNote(){
        //switching to the note activity of the first item
        onView(withId(R.id.recyclerView)).perform(
                RecyclerViewActions.actionOnItemAtPosition
                        (0, MyViewAction.clickChildViewWithId(R.id.note)));
        onView(withId(R.id.noteEditText)).check(matches(withText("")));
        onView(withId(R.id.noteTextView)).check(matches(withText("")));
        onView(withId(R.id.noteEditText)).perform(typeText("test"));
        onView(withId(R.id.submitNoteBtnDark)).perform(click());
        onView(withId(R.id.noteTextView)).check(matches(withText("test")));
        Espresso.pressBack();
    }

    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }

    //Helper method for getting child view from within recyclerview item
    public static class MyViewAction {

        public static ViewAction clickChildViewWithId(final int id) {
            return new ViewAction() {
                @Override
                public Matcher<View> getConstraints() {
                    return null;
                }

                @Override
                public String getDescription() {
                    return "Click on a child view with specified id.";
                }

                @Override
                public void perform(UiController uiController, View view) {
                    View v = view.findViewById(id);
                    v.performClick();
                }
            };
        }

    }

    public class RecyclerViewItemCountAssertion implements ViewAssertion {
        private final int expectedCount;

        public RecyclerViewItemCountAssertion(int expectedCount) {
            this.expectedCount = expectedCount;
        }

        @Override
        public void check(View view, NoMatchingViewException noViewFoundException) {
            if (noViewFoundException != null) {
                throw noViewFoundException;
            }

            RecyclerView recyclerView = (RecyclerView) view;
            RecyclerView.Adapter adapter = recyclerView.getAdapter();
            assertThat(adapter.getItemCount(), is(expectedCount));
        }
    }
}
