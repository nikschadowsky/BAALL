package de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.program;

import de.nikschadowsky.baall.compiler.output.BaallFileReference;
import de.nikschadowsky.baall.compiler.symbol.Token;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntax.tree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntax.tree.traversal.ASTVisitor;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnostic;
import de.nikschadowsky.baall.compiler.syntax.tree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @since 13.04.2024
 */
public class ImportsNodeImpl extends AbstractNode implements ImportsNode {

    private final List<BaallFileReference> imports = new ArrayList<>();

    public ImportsNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }


    @Override
    public @UnmodifiableView List<BaallFileReference> getImports() {
        return Collections.unmodifiableList(imports);
    }

    public void setImports(@NotNull List<Token> imports) {
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

        imports.stream().distinct().forEach(_import -> this.imports.add(new BaallFileReference(_import.value())));
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.IMPORT;
    }


    @Override
    public <D, R> Optional<R> accept(ASTVisitor<D, R> visitor, D data) {
        return visitor.visitImports(this, data);
    }
}
