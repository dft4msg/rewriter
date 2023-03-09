package com.yourorg;

import static org.openrewrite.java.Assertions.java;

import org.junit.jupiter.api.Test;
import org.openrewrite.java.JavaParser;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

public class ConcreteMenuRecipeTest implements RewriteTest {

    @Override
    public void defaults(RecipeSpec spec) {
        spec.recipe(new ConcreteMenuRecipe())
                .parser(JavaParser.fromJavaVersion()
                        .logCompilationWarningsAndErrors(true)
                // .classpath(Collections.singleton(Paths.get(".")))
                );
    }

    @Test
    void notReplacesOthers() {
        rewriteRun(
                java(
                        """
                                class Test {
                                    class MyButton extends BaseButton {

                                        void execute() {

                                        }

                                    }
                                }
                                """));
    }

    @Test
    void replacesBaseMenuWithBaseMultiSelectMenu() {
        rewriteRun(
                java(
                        """
                                    package com.yourorg.test;

                                    import com.yourorg.menu.BaseMenu;

                                    class Test {
                                        class MyDeleteMenu extends BaseMenu {

                                            boolean isMultiSelect() {
                                                return true;
                                            }

                                            void execute() {

                                            }

                                        }
                                    }
                                """,
                        """
                                    package com.yourorg.test;

                                    import com.yourorg.menu.BaseMultiSelectMenu;

                                    class Test {
                                        class MyDeleteMenu extends BaseMultiSelectMenu {

                                            void execute() {

                                            }

                                        }
                                    }
                                """));
    }

}
