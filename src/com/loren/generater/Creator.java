package com.loren.generater;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import org.apache.http.util.TextUtils;

/**
 * Created by loren on 2017/2/22.
 */
public class Creator extends WriteCommandAction.Simple {

    private Project project;
    private PsiFile file;
    private PsiClass targetClass;
    private PsiElementFactory factory;
    private String selectStr;
    private boolean isStatic = false;

    public Creator(Project project, PsiClass targetClass, PsiElementFactory factory, PsiFile... files) {
        super(project, files);
        this.project = project;
        this.file = files[0];
        this.targetClass = targetClass;
        this.factory = factory;
    }

    public void setSelectStr(String selectStr) {
        this.selectStr = selectStr.trim().replace(";", "").replaceAll(" ", "");
    }

    @Override
    protected void run() throws Throwable {
        try {
            if (TextUtils.isEmpty(selectStr.trim()) || !selectStr.contains("("))
                return;
            System.out.println("creator:" + selectStr);
            String funcName = selectStr.split("\\(")[0];
            if (selectStr.split("\\(")[1].replace(")", "").equals("")) {
                if (checkExist(funcName, new StringBuilder())) {
                    return;
                }
                StringBuilder func = new StringBuilder();
                func.append("private void " + funcName + "()");
                func.append(" {" + "\n\n" + "}");
                targetClass.add(factory.createMethodFromText(func.toString(), targetClass));
                return;
            }
            StringBuilder fields = new StringBuilder();
            for (int i = 0; i < selectStr.split("\\(")[1].split(",").length; i++) {
                String funtemp = ""; // field
                if (i == selectStr.split("\\(")[1].split(",").length - 1) {
                    funtemp = selectStr.split("\\(")[1].split(",")[i].replace(")", "").trim();
                } else {
                    funtemp = selectStr.split("\\(")[1].split(",")[i].trim();
                }
                if (TextUtils.isEmpty(funtemp))
                    return;
                System.out.println("funTemp:" + funtemp);
                String type = "";
                if (TextUtils.isEmpty(targetClass.findFieldByName(funtemp, true).getType().toString()))
                    type = "Object";
                else
                    type = targetClass.findFieldByName(funtemp, true).getType().toString().split(":")[1];
                System.out.println("type:" + type);
                fields.append(type + " " + funtemp + (i == selectStr.split("\\(")[1].split(",").length - 1 ? "" : ", "));
            }

            StringBuilder func = new StringBuilder();
//        func.append("private " + (isStatic ? "static " : "") + "void " + funcName + "(" + fields.toString() + ")");
            func.append("private void " + funcName + "(" + fields.toString() + ")");
            func.append(" {" + "\n\n" + "}");

            if (checkExist(funcName, fields)) {
                return;
            }
            targetClass.add(factory.createMethodFromText(func.toString(), targetClass));

            //导入需要的类
//        JavaCodeStyleManager styleManager = JavaCodeStyleManager.getInstance(project);
//        styleManager.optimizeImports(file);
//        styleManager.shortenClassReferences(targetClass);
        } catch (NullPointerException e1) {
            e1.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException e2) {
            e2.printStackTrace();
        }
    }

    private boolean checkExist(String funcName, StringBuilder fields) {
        boolean exist = false;
        PsiMethod[] method = targetClass.getAllMethods();
        if (method.length == 0) {
            exist = true;
        }
        for (PsiMethod psiMethod : method) {
            if (psiMethod.getParameterList().toString().split(":").length > 1) {
                if ((psiMethod.getName() + psiMethod.getParameterList().toString().split(":")[1]).equals(funcName + "(" + fields.toString() + ")")) {
                    exist = true;
//                    isStatic = psiMethod.getModifierList().hasModifierProperty("static");
                    break;
                }
            }
        }
        return exist;
    }
}
