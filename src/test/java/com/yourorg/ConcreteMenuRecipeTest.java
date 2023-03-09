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
                                class Test {
                                    class MyButton extends BaseButton {

                                        @Override
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

                                    class Test {
                                        class MyDeleteMenu extends com.yourorg.menu.BaseMultiSelectMenu {

                                            @Override
                                            protected void execute() {

                                            }

                                        }
                                    }
                                """));
    }

}
