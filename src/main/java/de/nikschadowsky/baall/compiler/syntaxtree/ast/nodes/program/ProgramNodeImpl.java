package de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.program;

import de.nikschadowsky.baall.compiler.syntaxtree.ast.NodeType;
import de.nikschadowsky.baall.compiler.syntaxtree.ast.nodes.AbstractNode;
import de.nikschadowsky.baall.compiler.syntaxtree.util.NodeDiagnosticCollector;
import org.jetbrains.annotations.NotNull;

/**
 * File created on 14.04.2024
 */
public class ProgramNodeImpl extends AbstractNode implements ProgramNode {

    private ImportsNode imports;

    private StatementsNode statements;

    private ExportsNode exports;

    public ProgramNodeImpl(NodeDiagnosticCollector diagnostics) {
        super(diagnostics);
    }

    @Override
    public ImportsNode getImports() {
        return imports;
    }

    public void setImports(ImportsNode imports) {
        this.imports = imports;
    }

    @Override
    public StatementsNode getStatements() {
        return statements;
    }

    public void setStatements(StatementsNode statements) {
        this.statements = statements;
    }

    @Override
    public ExportsNode getExports() {
        return exports;
    }

    public void setExports(ExportsNode exports) {
        this.exports = exports;
    }

    @Override
    public @NotNull NodeType getNodeType() {
        return NodeType.PROGRAM;
    }
}
