package com.yourorg;

import java.util.List;
import java.util.stream.Collectors;

import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.java.AddImport;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.RemoveImport;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.tree.J.MethodDeclaration;
import org.openrewrite.java.tree.JavaType.ShallowClass;
import org.openrewrite.java.tree.Statement;
import org.openrewrite.java.tree.TypeTree;
import org.openrewrite.java.tree.TypeUtils;

public class ConcreteMenuRecipe extends Recipe {

    @Override
    public String getDisplayName() {
        return "Convert to Concrete Menu";
    }

    @Override
    public String getDescription() {
        return "Replace BaseMenu with concrete one, e.g. BaseMultiSelectMenu.";
    }

    @Override
    protected TreeVisitor<?, ExecutionContext> getVisitor() {
        return new MyVisitor();
    }

    private static class MyVisitor extends JavaIsoVisitor<ExecutionContext> {

        final String baseMenuFqn = "com.yourorg.menu.BaseMenu";

        final String multiMenuFqn = "com.yourorg.menu.BaseMultiSelectMenu";

        final String multiMethod = "isMultiSelect";

        final String singleMethod = "isSingleSelect";

        final static String RETURN_TRUE = "return true";

        final static String RETURN_FALSE = "return false";

        @Override
        public J.ClassDeclaration visitClassDeclaration(J.ClassDeclaration classDecl, ExecutionContext p) {
            J.ClassDeclaration cd = super.visitClassDeclaration(classDecl, p);

            if (cd.getExtends() == null) {
                return cd;
            }
            if (!TypeUtils.isOfClassType(cd.getExtends().getType(), baseMenuFqn)) {
                return cd;
            }

            List<MethodDeclaration> declarations = getMethods(cd);

            Boolean multiSelection = findMethodReturnValue(declarations, multiMethod);
            Boolean singleSelection = findMethodReturnValue(declarations, singleMethod);
            Boolean noSelection = null;

            System.out.println("multi=" + multiSelection + ", single=" + singleSelection + ", no=" + noSelection);

            if (Boolean.TRUE.equals(multiSelection)) {
                cd = addExtendsMenu(cd, multiMenuFqn);
                cd = removeMethod(cd, multiMethod);

                addMenuImport(multiMenuFqn);
                removeOldMenuImport();

                return cd;
            }

            return cd;
        }

        private List<MethodDeclaration> getMethods(J.ClassDeclaration cd) {
            List<MethodDeclaration> declarations = cd.getBody().getStatements().stream()
                    .filter(s -> s instanceof J.MethodDeclaration)
                    .map(J.MethodDeclaration.class::cast)
                    .collect(Collectors.toList());
            return declarations;
        }

        private void removeOldMenuImport() {
            doAfterVisit(new RemoveImport<>(baseMenuFqn));
        }

        private void addMenuImport(String menuFqn) {
            doAfterVisit(new AddImport<>(menuFqn, null, false));
        }

        private J.ClassDeclaration addExtendsMenu(J.ClassDeclaration cd, String menuFqn) {
            TypeTree multiMenu = TypeTree.build(simpleClassName(menuFqn))
                    .withType(ShallowClass.build(menuFqn));

            // add space between extends and the Menu
            cd = cd.withExtends(multiMenu.withPrefix(cd.getExtends().getPrefix()));
            return cd;
        }

        private J.ClassDeclaration removeMethod(J.ClassDeclaration cd, String methodName) {
            List<Statement> statements = cd.getBody().getStatements();

            statements.removeIf(s -> s instanceof J.MethodDeclaration
                    && ((J.MethodDeclaration) s).getSimpleName().equals(methodName));

            cd = cd.withBody(cd.getBody().withStatements(statements));
            return cd;
        }

        private Boolean findMethodReturnValue(List<MethodDeclaration> declarations, String methodName) {
            for (J.MethodDeclaration methodDeclaration : declarations) {
                if (methodName.equals(methodDeclaration.getSimpleName())) {
                    List<Statement> statements = methodDeclaration.getBody().getStatements();
                    for (Statement statement : statements) {
                        if (RETURN_FALSE.equals(statement.toString())) {
                            return false;
                        } else if (RETURN_TRUE.equals(statement.toString())) {
                            return true;
                        }
                    }
                }
            }

            return null;
        }

        private String simpleClassName(String fqn) {
            String[] pts = fqn.split("\\.");
            return pts[pts.length - 1];
        }
    };

}
