# PrinterWebContent
Wifi/Bluetooth连接打印机打印Webview的内容


//Wifi连接
在Android 4.4（API级别19）及更高版本中，框架提供了直接从Android应用程序打印图像和文档的服务。
  
*打印WebView并加载HTML内容
在创建WebView并加载HTML内容后，您的应用程序几乎完成了它的打印过程的一部分。 接下来的步骤是访问PrintManager，创建打印适配器，最后创建打印作业。 以下示例说明如何执行这些步骤：
private void createWebPrintJob(WebView webView) {

    // Get a PrintManager instance
    PrintManager printManager = (PrintManager) getActivity()
            .getSystemService(Context.PRINT_SERVICE);

    // Get a print adapter instance
    PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter();

    // Create a print job with name and adapter instance
    String jobName = getString(R.string.app_name) + " Document";
    PrintJob printJob = printManager.print(jobName, printAdapter,
            new PrintAttributes.Builder().build());

    // Save the job object for later status checking
    mPrintJobs.add(printJob);
} 此示例保存PrintJob对象的实例以供应用程序使用，这不是必需的。 您的应用程序可能会使用此对象来跟踪打印作业正在处理时的进度。 当您希望在应用程序中监视打印作业的完成，失败或用户取消操作的状态时，此方法非常有用。 不需要创建应用程序内通知，因为打印框架会自动为打印作业创建系统通知。




*打印照片
拍摄和分享照片是移动设备最流行的用途之一。 如果应用程序拍摄照片，显示照片或允许用户共享图像，则应考虑在应用程序中启用打印这些图像。 Android Support Library 提供了一个方便的功能，使用最少的代码和简单的打印布局选项集启用图像打印。
本节将向您介绍如何使用v4支持库PrintHelper 类打印图像：
Android支持库PrintHelper类提供了一种打印图像的简单方法。 该类有一个单一的布局选项，setScaleMode（），它允许您打印两个选项之一：

SCALE_MODE_FIT - 此选项调整图像大小，以便整个图像显示在页面的可打印区域内。
SCALE_MODE_FILL - 此选项缩放图像，以便它填充页面的整个可打印区域。 选择此设置意味着不打印图像的顶部和底部，或左右边缘的某些部分。 如果未设置缩放模式，则此选项为默认值。
setScaleMode（）的缩放选项保持图像的现有宽高比不变。 以下代码示例显示如何创建PrintHelper类的实例，设置缩放选项，并开始打印过程：
private void doPhotoPrint() {
    PrintHelper photoPrinter = new PrintHelper(getActivity());
    photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
    Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
            R.drawable.droids);
    photoPrinter.printBitmap("droids.jpg - test print", bitmap);
}此方法可以称为菜单项的操作。 注意，不总是支持的操作（例如打印）的菜单项应该放在溢出菜单中。 有关更多信息，请参阅Action Bar设计指南。

调用printBitmap（）方法后，不需要从应用程序执行进一步的操作。 将显示Android打印用户界面，允许用户选择打印机和打印选项。 然后用户可以打印图像或取消操作。 如果用户选择打印图像，则会创建打印作业，并在系统栏中显示打印通知。


