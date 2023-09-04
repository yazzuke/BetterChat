        package com.example.betterchat;

        import androidx.test.espresso.Espresso;
        import androidx.test.espresso.action.ViewActions;
        import androidx.test.espresso.assertion.ViewAssertions;
        import androidx.test.espresso.matcher.ViewMatchers;
        import androidx.test.ext.junit.rules.ActivityScenarioRule;
        import androidx.test.ext.junit.runners.AndroidJUnit4;

        import org.junit.Rule;
        import org.junit.Test;
        import org.junit.runner.RunWith;

        @RunWith(AndroidJUnit4.class)
        public class ActivityLoginEspressoTest {

            @Rule
            public ActivityScenarioRule<Activity_Login> activityScenarioRule = new ActivityScenarioRule<>(Activity_Login.class);

            @Test
            public void testLogin() {
                // Realiza acciones en los elementos de la interfaz de usuario utilizando Espresso
                Espresso.onView(ViewMatchers.withId(R.id.EditText_EmailLogin))
                        .perform(ViewActions.typeText("kendrick@gmail.com"), ViewActions.closeSoftKeyboard());
                Espresso.onView(ViewMatchers.withId(R.id.EditText_PasswordLogin))
                        .perform(ViewActions.typeText("gato123"), ViewActions.closeSoftKeyboard());
                Espresso.onView(ViewMatchers.withId(R.id.button_InicioSesion)).perform(ViewActions.click());

                // Verifica la visibilidad de un elemento en Activity_Login después de iniciar sesión
                Espresso.onView(ViewMatchers.withId(R.id.button_InicioSesion))
                        .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
            }
        }