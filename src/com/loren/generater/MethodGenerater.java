package com.loren.generater;

import com.intellij.codeInsight.CodeInsightActionHandler;
import com.intellij.codeInsight.generation.actions.BaseGenerateAction;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiUtilBase;
import com.intellij.ui.JBColor;
import org.apache.http.util.TextUtils;

import java.awt.*;

/**
 * Created by loren on 2017/2/22.
 */
public class MethodGenerater extends BaseGenerateAction {
    //    LineNumberReader lineNumberReader;
//    FileReader fileReader;
//    private File source;
    private String selectStr = "";
    private Creator creator;

    public MethodGenerater() {
        super(null);
    }

    public MethodGenerater(CodeInsightActionHandler handler) {
        super(handler);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(PlatformDataKeys.PROJECT);
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        PsiFile file = PsiUtilBase.getPsiFileInEditor(editor, project);

        CaretModel caretModel = editor.getCaretModel();
        LogicalPosition logicalPosition = caretModel.getLogicalPosition();

        PsiClass targetClass = getTargetClass(editor, file);
        PsiElementFactory factory = JavaPsiFacade.getElementFactory(project);

        creator = new Creator(project, targetClass, factory, file);
        SelectionModel selectionModel = editor.getSelectionModel();
        selectStr = selectionModel.getSelectedText();
//        source = new File(file.getContainingDirectory().toString().split(":")[1]
//                + "/" + targetClass.getName() + "." + file.getFileType().getName().toLowerCase());

        int numberLine = logicalPosition.line;
        if (TextUtils.isEmpty(selectStr)) {
//            readLineToString(numberLine);
            caretModel.getCurrentCaret().selectLineAtCaret();
            selectStr = caretModel.getCurrentCaret().getSelectedText();
            editor.getCaretModel().getCurrentCaret().removeSelection();
            showPopupBalloon(editor, selectStr + "\n第" + (numberLine + 1) + "行");
        } else {
            showPopupBalloon(editor, selectStr);
        }

        creator.setSelectStr(selectStr);
        creator.execute();
        selectStr = "";
    }

    private void showPopupBalloon(final Editor editor, final String result) {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
            public void run() {
                JBPopupFactory factory = JBPopupFactory.getInstance();
                factory.createHtmlTextBalloonBuilder(result, null, new JBColor(new Color(186, 238, 186), new Color(73, 117, 73)), null)
                        .setFadeoutTime(3000)
                        .createBalloon()
                        .show(factory.guessBestPopupLocation(editor), Balloon.Position.below);
            }
        });
    }


//    private void readLineToString(int numberLine) {
//        try {
//            fileReader = new FileReader(source);
//            lineNumberReader = new LineNumberReader(fileReader);
//            int line = 0;
//            while (line < numberLine) {
//                lineNumberReader.readLine();
//                line++;
//            }
//            selectStr = lineNumberReader.readLine();
//            System.out.println(selectStr);
//            fileReader.close();
//            lineNumberReader.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//
//    }
}
