package com.yourorg;

import static org.openrewrite.java.Assertions.java;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.openrewrite.java.JavaParser;
import org.openrewrite.test.RecipeSpec;
import org.openrewrite.test.RewriteTest;

import io.github.classgraph.ClassGraph;

public class ConcreteMenuRecipeTest implements RewriteTest {

    @Override
    public void defaults(RecipeSpec spec) {
        List<Path> classpath = new ClassGraph().getClasspathURIs().stream().map(Paths::get).toList();

        spec.recipe(new ConcreteMenuRecipe())
                .parser(JavaParser.fromJavaVersion()
                        .logCompilationWarningsAndErrors(true)
                        .classpath(classpath)
                );
    }

    @Test
    void notReplacesOthers() {
        rewriteRun(
                java(
                        """
                            package com.yourorg.test;

                            import com.yourorg.button.BaseButton;

                                class Test {
                                    class MyButton extends BaseButton {

                                        @Override
                                        protected void execute() {

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

                                            @Override
                                            protected boolean isMultiSelect() {
                                                return true;
                                            }

                                            @Override
                                            protected void execute() {

                                            }

                                        }
                                    }
                                """,
                        """
                                    package com.yourorg.test;

                                    import com.yourorg.menu.BaseMultiSelectMenu;

                                    class Test {
                                        class MyDeleteMenu extends BaseMultiSelectMenu {

                                            @Override
                                            protected void execute() {

                                            }

                                        }
                                    }
                                """));
    }

    @Test
    void replacesBaseMenuWithBaseMultiSelectMenuAndRemoveOtherMenuMethods() {
        rewriteRun(
                java(
                        """
                                    package com.yourorg.test;

                                    import com.yourorg.menu.BaseMenu;

                                    class Test {
                                        class MyDeleteMenu extends BaseMenu {

                                            @Override
                                            protected boolean isMultiSelect() {
                                                return true;
                                            }

                                            @Override
                                            protected boolean isSingleSelect() {
                                                return true;
                                            }

                                            @Override
                                            protected boolean isEmptySelect() {
                                                return false;
                                            }

                                            @Override
                                            protected void execute() {

                                            }

                                        }
                                    }
                                """,
                        """
                                    package com.yourorg.test;

                                    import com.yourorg.menu.BaseMultiSelectMenu;

                                    class Test {
                                        class MyDeleteMenu extends BaseMultiSelectMenu {

                                            @Override
                                            protected void execute() {

                                            }

                                        }
                                    }
                                """));
    }

    @Test
    void replacesBaseMenuWithBaseSingleSelectMenu() {
        rewriteRun(
                java(
                        """
                                    package com.yourorg.test;

                                    import com.yourorg.menu.BaseMenu;

                                    class Test {
                                        class MyEditMenu extends BaseMenu {

                                            @Override
                                            protected boolean isMultiSelect() {
                                                return false;
                                            }

                                            @Override
                                            protected boolean isSingleSelect() {
                                                return true;
                                            }

                                            @Override
                                            protected void execute() {

                                            }

                                        }
                                    }
                                """,
                        """
                                    package com.yourorg.test;

                                    import com.yourorg.menu.BaseSingleSelectMenu;

                                    class Test {
                                        class MyEditMenu extends BaseSingleSelectMenu {

                                            @Override
                                            protected void execute() {

                                            }

                                        }
                                    }
                                """));
    }

    @Test
    void replacesBaseMenuWithBaseEmptySelectMenu() {
        rewriteRun(
                java(
                        """
                                    package com.yourorg.test;

                                    import com.yourorg.menu.BaseMenu;

                                    class Test {
                                        class MyNewMenu extends BaseMenu {

                                            @Override
                                            protected boolean isMultiSelect() {
                                                return false;
                                            }

                                            @Override
                                            protected boolean isSingleSelect() {
                                                return false;
                                            }

                                            @Override
                                            protected boolean isEmptySelect() {
                                                return true;
                                            }

                                            @Override
                                            protected void execute() {

                                            }

                                        }
                                    }
                                """,
                        """
                                    package com.yourorg.test;

                                    import com.yourorg.menu.BaseEmptySelectMenu;

                                    class Test {
                                        class MyNewMenu extends BaseEmptySelectMenu {

                                            @Override
                                            protected void execute() {

                                            }

                                        }
                                    }
                                """));
    }

}
