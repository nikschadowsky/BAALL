package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program;

import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnostic;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeUtility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @since 13.04.2024
 */
public class ImportsNodeImpl extends AbstractNode implements ImportsNode {

    private List<Token> imports;

    public ImportsNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }


    @Override
    public @UnmodifiableView List<Token> getImports() {
        return NodeUtility.toUnmodifiableList(imports);
    }

    public void setImports(List<Token> imports) {
        this.imports = new LinkedList<>(imports);

        if (imports.isEmpty()) {
            getDiagnosticCollector().report(new NodeDiagnostic(
                    "No imports defined",
                    null,
                    NodeDiagnostic.ReportingLevel.INFO
            ));
            return;
        }

        imports.stream()
               .filter(e -> Collections.frequency(imports, e) > 1)
               .forEach(e -> getDiagnosticCollector().report(new NodeDiagnostic(
                       "Duplicate import",
                       e,
                       NodeDiagnostic.ReportingLevel.WARN
               )));
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.IMPORT;
    }
}
