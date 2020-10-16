import com.intellij.ide.fileTemplates.FileTemplateManager
import com.intellij.ide.fileTemplates.FileTemplateUtil
import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.ui.DialogBuilder
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import com.intellij.psi.codeStyle.CodeStyleManager


class GenerateAction : AnAction() {
    companion object {
        const val SPECIAL_DIRECTORY_NAME = "java"
        private const val ITEM_TEMPLATE_NAME = "DataItem.kt"
        private const val ITEM_LAYOUT_TEMPLATE_NAME = "DataItem.xml"
    }

    override fun actionPerformed(event: AnActionEvent) {
        val file = CommonDataKeys.VIRTUAL_FILE.getData(event.dataContext)
        if (file == null) {
            notificationError("无法在此处创建文件：${file?.name}")
            return
        }

        println("file ${file.name}")

        val packageName = if (file.isDirectory)
            getPackage(file)
        else
            getPackage(file.parent)

        if (packageName == null) {
            notificationError("getPackage failed ： ${file.name}")
            return
        }

        val boardFrame = GenerateBoardFrame(packageName)
        DialogBuilder(getEventProject(event)).apply {
            setTitle("MultiItemTemplates")
            setCenterPanel(boardFrame.`$$$getRootComponent$$$`())
            addCancelAction()
            cancelAction.setText("cancel")
            addOkAction()
            okAction.setText("finish")
            setOkOperation {
                generateDataItem(event, boardFrame)?.let {
                    notificationError("创建 Item 失败：$it")
                }
                generateItemLayout(event, boardFrame)?.let {
                    notificationError("创建 Layout 失败：$it")
                }
                dialogWrapper.close(DialogWrapper.OK_EXIT_CODE, true)
            }
            show()
        }
    }

    private fun generateItemLayout(event: AnActionEvent, boardFrame: GenerateBoardFrame): String? {
        val view = LangDataKeys.IDE_VIEW.getData(event.dataContext) ?: return "con not find IDE view "

        fun getMain(directory: PsiDirectory?): PsiDirectory? =
            if (directory == null || directory.name == SPECIAL_DIRECTORY_NAME) directory?.parent
            else getMain(directory.parent)


        val directory =
            getMain(view.orChooseDirectory)?.findSubdirectory("res")?.findSubdirectory("layout")
                ?: return "con not find directory '/src/layout' "


        val project = getEventProject(event) ?: return "con not find Project "

        val fileTemplate = FileTemplateManager.getInstance(project).getInternalTemplate(ITEM_LAYOUT_TEMPLATE_NAME)

        try {
            val psiElement = FileTemplateUtil.createFromTemplate(fileTemplate, boardFrame.layoutName, null, directory)

            onProcessItemLayout(boardFrame.fullName, psiElement.containingFile)
        } catch (e: Exception) {
            return e.message
        }
        return null
    }

    private fun generateDataItem(event: AnActionEvent, boardFrame: GenerateBoardFrame): String? {
        val view = LangDataKeys.IDE_VIEW.getData(event.dataContext) ?: return "con not find IDE view  "

        val directory = view.orChooseDirectory ?: return "con not find current directory "
        val project = getEventProject(event) ?: return "con not find Project "

        val fileTemplate = FileTemplateManager.getInstance(project).getInternalTemplate(ITEM_TEMPLATE_NAME)

        try {
            val psiElement = FileTemplateUtil.createFromTemplate(fileTemplate, boardFrame.itemName, null, directory)
            onProcessDataItem(boardFrame.layoutName, boardFrame.packageName, psiElement.containingFile)
        } catch (e: Exception) {
            return e.message
        }
        return null

    }

    private fun getPackage(file: VirtualFile?): String? {
        val canonicalPath = file?.canonicalPath ?: return null
        val startIndex = canonicalPath.indexOf(SPECIAL_DIRECTORY_NAME)
        if (startIndex == -1) {
            return null
        }
        val packageName = canonicalPath.substring(startIndex + SPECIAL_DIRECTORY_NAME.length + 1, canonicalPath.length)
            .replace("/", ".")
        println("file $canonicalPath $packageName")
        return packageName

    }

    private fun notificationError(value: String) {
        Notifications.Bus.notify(
            Notification(
                "MultiItemTemplates", "File Path Error",
                value, NotificationType.ERROR
            )
        )
    }


    private fun onProcessDataItem(layoutName: String, packageName: String, itemClass: PsiFile) {
        val file = itemClass.containingFile
        val manager = PsiDocumentManager.getInstance(itemClass.project)
        val document = manager.getDocument(file) ?: return
        WriteCommandAction.writeCommandAction(itemClass.project, itemClass)
            .run<Exception> {
                manager.doPostponedOperationsAndUnblockDocument(document)
                document.setText(
                    document.text
                        .replace("MULTI_ITEM_LAYOUT", layoutName)
                        .replace("MULTI_ITEM_PACKAGE", packageName)
                )
                CodeStyleManager.getInstance(itemClass.project).reformat(itemClass)
            }
    }

    private fun onProcessItemLayout(layoutName: String, itemClass: PsiFile) {
        val file = itemClass.containingFile
        val manager = PsiDocumentManager.getInstance(itemClass.project)
        val document = manager.getDocument(file) ?: return
        WriteCommandAction.writeCommandAction(itemClass.project, itemClass)
            .run<Exception> {
                manager.doPostponedOperationsAndUnblockDocument(document)
                document.setText(
                    document.text
                        .replace("MULTI_ITEM_FULL_NAME", layoutName)
                )
                CodeStyleManager.getInstance(itemClass.project).reformat(itemClass)
            }
    }

}