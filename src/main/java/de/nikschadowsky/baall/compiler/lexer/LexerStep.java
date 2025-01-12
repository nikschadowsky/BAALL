package de.nikschadowsky.baall.compiler.lexer;


import de.nikschadowsky.baall.compiler.CompileInformation;
import de.nikschadowsky.baall.compiler.Step;
import de.nikschadowsky.baall.compiler.StepOptions;
import de.nikschadowsky.baall.compiler.lexer.error.LexerDiagnostic;
import de.nikschadowsky.baall.compiler.output.error.CompileErrorFactory;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LexerStep extends Step<List<String>, List<LexerRow>> {

    /**
     * Blocks used to preprocess source code
     */
    private final static List<InputComponentBlock> BLOCKS =
            List.of(
                    new InputComponentBlock("\"", "\"", true),
                    new InputComponentBlock("'", "'", true),
                    new InputComponentBlock("/*", "*/", false),
                    new InputComponentBlock("//", "\n", false)
            );


    private String fileContent;

    private List<LexerRow> preprocessedRows;
    private List<LexerRow> unprocessedLines;

    public LexerStep(@Nullable StepOptions options) {
        super(options);
    }

    @Override
    public List<LexerRow> executeStep(List<String> lines, CompileInformation compileInformation) {
        unprocessedLines = new ArrayList<>();
        IntStream.range(0, lines.size()).forEach(i -> unprocessedLines.add(new LexerRow(lines.get(i), i)));

        compileInformation.addInformation("Source code", fileContent);

        preprocessCode();
        compileInformation.addInformation(
                "Preprocessed code",
                preprocessedRows.stream()
                                .sorted(Comparator.comparingInt(LexerRow::rowIndex))
                                .map(LexerRow::content)
                                .collect(Collectors.joining("\n"))
        );

        return new ArrayList<>(preprocessedRows);
    }

    private void preprocessCode() {
        preprocessedRows = new ArrayList<>(unprocessedLines);
        Stack<RemoveEntry> edits = new Stack<>();
        InputComponentBlock activeBlock = null;
        for (int i = 0; i < preprocessedRows.size(); i++) {
            int searchEndIndexWithInRow = 0;
            LexerRow row = preprocessedRows.get(i);

            if (activeBlock == null) {
                var optFirstBlock = findFirstOccurrence(row);
                if (optFirstBlock.isPresent()) {
                    activeBlock = optFirstBlock.get();

                    int index = row.content().indexOf(activeBlock.start());
                    edits.push(new RemoveEntry(i, index, activeBlock));
                    // remember index from which to continue search for end marker
                    searchEndIndexWithInRow = index + activeBlock.start().length();
                }
            }

            if (activeBlock != null) {
                // end marker for new row always ends at the end of the row
                if (activeBlock.end().equals("\n")) {
                    edits.push(new RemoveEntry(i, row.length() - 1, activeBlock));
                    activeBlock = null;
                    continue;
                }

                // start searching
                int endIndex = row.content().indexOf(activeBlock.end(), searchEndIndexWithInRow);
                if (endIndex != -1) {
                    edits.push(new RemoveEntry(i, endIndex + activeBlock.end().length() - 1, activeBlock));
                    activeBlock = null;
                }
            }
        }

        // stack size is odd -> open end -> add closing element
        if (edits.size() % 2 == 1) {
            assert activeBlock != null;
            edits.push(new RemoveEntry(
                    preprocessedRows.size() - 1,
                    preprocessedRows.get(preprocessedRows.size() - 1).length(),
                    activeBlock
            ));
            CompileErrorFactory.createLexerCompileError(new LexerDiagnostic(
                    "Missing ending sequence: " + activeBlock.end(),
                    preprocessedRows.get(
                            preprocessedRows.size() - 1)
            ));
        }

        while (!edits.isEmpty()) {
            RemoveEntry end = edits.pop();
            editRowsForEntry(edits.pop(), end);
        }

        removeEmptyRows();
    }

    private void removeEmptyRows() {
        preprocessedRows =
                preprocessedRows.stream().filter(row -> !row.content().isBlank()).collect(Collectors.toList());
    }

    private void editRowsForEntry(RemoveEntry start, RemoveEntry end) {
        int startRow;
        int startIndex;
        int endRow;
        int endIndex;

        startRow = start.row();
        startIndex = start.index();

        endRow = end.row();
        endIndex = end.index();

        if (!start.block().keepInInput()) {
            // edit last line's content
            LexerRow firstRow = preprocessedRows.get(startRow);
            LexerRow lastRow = preprocessedRows.get(endRow);

            String newContent;

            if (startRow == endRow) {
                newContent =
                        lastRow.content().substring(0, startIndex) + lastRow.content().substring(endIndex + 1);
                preprocessedRows.set(startRow, new LexerRow(newContent.trim(), startRow));
            } else {
                newContent = firstRow.content().substring(0, startIndex);
                preprocessedRows.set(startRow, new LexerRow(newContent.trim(), firstRow.rowIndex()));
                for (int i = 1; i < endRow - startRow; i++) {
                    preprocessedRows.set(startRow + i, new LexerRow("", preprocessedRows.get(startRow + i).rowIndex()));
                }
                newContent = lastRow.content().substring(endIndex + 1);
                preprocessedRows.set(endRow, new LexerRow(newContent.trim(), lastRow.rowIndex()));
            }

        }
    }

    private Optional<InputComponentBlock> findFirstOccurrence(LexerRow row) {
        int minIndex = Integer.MAX_VALUE;
        InputComponentBlock firstBlock = null;

        for (InputComponentBlock block : LexerStep.BLOCKS) {
            int index = row.content().indexOf(block.start());

            if (index == -1) {
                continue;
            }

            if (index < minIndex) {
                firstBlock = block;
                minIndex = index;
            }
        }
        return Optional.ofNullable(firstBlock);
    }

    private record RemoveEntry(int row, int index, InputComponentBlock block) {
    }
}
