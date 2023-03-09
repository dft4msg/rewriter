package com.yourorg;

import java.util.List;
import java.util.stream.Collectors;

import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.TreeVisitor;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.RemoveImport;
import org.openrewrite.java.tree.J;
import org.openrewrite.java.tree.J.MethodDeclaration;
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
        return new JavaIsoVisitor<ExecutionContext>() {
            @Override
            public J.ClassDeclaration visitClassDeclaration(J.ClassDeclaration classDecl, ExecutionContext p) {
                J.ClassDeclaration cd = super.visitClassDeclaration(classDecl, p);

                if (cd.getExtends() == null) {
                    return cd;
                }

                boolean match = TypeUtils.isOfClassType(cd.getExtends().getType(), "com.yourorg.menu.BaseMenu");
                System.out.println(match);

                if (!"BaseMenu".equals(cd.getExtends().toString())) {
                    return cd;
                }

                System.out.println("found BaseMenu");

                List<MethodDeclaration> declarations = cd.getBody().getStatements().stream()
                        .filter(s -> s instanceof J.MethodDeclaration)
                        .map(J.MethodDeclaration.class::cast)
                        .collect(Collectors.toList());

                Boolean multiSelection = null;
                Boolean singleSelection = null;
                Boolean noSelection = null;

                for (J.MethodDeclaration methodDeclaration : declarations) {
                    if ("isMultiSelect".equals(methodDeclaration.getSimpleName())) {
                        // TODO extract this
                        List<Statement> statements = methodDeclaration.getBody().getStatements();
                        for (Statement statement : statements) {
                            if ("return false".equals(statement.toString())) {
                                multiSelection = false;
                                break;
                            } else if ("return true".equals(statement.toString())) {
                                multiSelection = true;
                                break;
                            }
                        }
                    }
                }

                System.out.println("multi=" + multiSelection + ", single=" + singleSelection + ", no=" + noSelection);

                if (Boolean.TRUE.equals(multiSelection)) {
                    cd = cd.withExtends(TypeTree.build("com.yourorg.menu.BaseMultiSelectMenu")
                            .withPrefix(cd.getExtends().getPrefix()));

                    List<Statement> statements = cd.getBody().getStatements();

                    statements.removeIf(s -> s instanceof J.MethodDeclaration
                            && ((J.MethodDeclaration) s).getSimpleName().equals("isMultiSelect"));

                    cd = cd.withBody(cd.getBody().withStatements(statements));

                    // doAfterVisit(new AddImport<>("com.yourorg.menu.BaseMultiSelectMenu", null, false));
                    doAfterVisit(new RemoveImport<>("com.yourorg.menu.BaseMenu"));

                    return cd;
                }

                return cd;
            }

        };
    }

}
