#include "qeowebviewjavascriptinterface.h"
#include "otcdialog.h"
#include <QDebug>
#include <QGraphicsWebView>
#include <QWebFrame>
#include <QMessageBox>
#include <QStyle>
#include <QThread>

//---------------------------------------------
// Callbacks from qeo-c-utils library
//---------------------------------------------
qeo_util_retcode_t QeoWebViewJavascriptInterface::on_registration_params_needed(uintptr_t app_context, qeo_platform_security_context_t sec_context)
{
    qDebug() << "WebView [" << QThread::currentThreadId() << "]: " << __func__;

    QeoWebViewJavascriptInterface* webBridgeObj = (QeoWebViewJavascriptInterface*)app_context;
    emit webBridgeObj->invokeRegistrationDialog(sec_context);

    return QEO_UTIL_OK;
}

void QeoWebViewJavascriptInterface::on_security_status_update_cb(uintptr_t app_context, qeo_platform_security_context_t sec_context, qeo_platform_security_state state, qeo_platform_security_state_reason reason)
{
    /* Nothing special done here */
    (void)app_context;
    (void)sec_context;
    (void)state;
    (void)reason;

    qDebug() << __func__;
}

//---------------------------------------------
// Callback from qeo library
//---------------------------------------------
void QeoWebViewJavascriptInterface::on_json_async_event(const qeo_json_async_ctx_t *ctx, uintptr_t userdata, const char *id, const char *event, const char *json_data)
{
}

QeoWebViewJavascriptInterface::QeoWebViewJavascriptInterface(QGraphicsWebView *webView, QObject *parent) :
    QObject(parent),mWebView(webView)
{
    qDebug() << "WebView[" << QThread::currentThreadId() << "]: C'tor" << __func__ ;
	
    connect(this,SIGNAL(invokeJavaScriptCall(QString)),this,SLOT(slot_JavaScriptCall(QString)),Qt::QueuedConnection);                               
    //connect(this,SIGNAL(invokeRegistrationDialog(qeo_platform_security_context_t)),this,SLOT(slot_ShowRegistrationDialog(qeo_platform_security_context_t)),Qt::QueuedConnection);
    connect(this,SIGNAL(invokeMessageBox(QString,bool)),this,SLOT(slot_ShowMessageBox(QString,bool)),Qt::QueuedConnection);
}

QeoWebViewJavascriptInterface::~QeoWebViewJavascriptInterface() {
    qDebug() << "WebView: " << __func__ ;

	// Remove all signal-slot connections
    disconnect();
}

void QeoWebViewJavascriptInterface::slot_JavaScriptCall(QString javascript) 
{
	printf("Javascript call\n");
	mWebView->page()->mainFrame()->evaluateJavaScript(javascript);
}


void QeoWebViewJavascriptInterface::slot_ShowRegistrationDialog(qeo_platform_security_context_t sec_context) 
{
    qDebug() << "WebView[" << QThread::currentThreadId() << "]: " << __func__ ;

    // Setup dialg for security credentials
    OtcDialog *otcDialog = new OtcDialog(sec_context, NULL);
    otcDialog->setAttribute(Qt::WA_ShowModal,true);
    otcDialog->setAttribute(Qt::WA_DeleteOnClose,true);
    otcDialog->show();
}

void QeoWebViewJavascriptInterface::slot_ShowMessageBox(QString message,bool shutdown) {
    qDebug() << "WebView: " << __func__ ;

    QMessageBox msgBox;
    msgBox.setWindowTitle("Error Notification");
    QString msg = QString("<p align='center'>%1</p>").arg(message);
    msgBox.setText(msg);
    QStyle* style = msgBox.style();
    QIcon tmpIcon = style->standardIcon(QStyle::SP_MessageBoxCritical,0,&msgBox);
    msgBox.setWindowIcon(tmpIcon);
    msgBox.addButton(trUtf8("Close"), QMessageBox::YesRole);
    msgBox.show();
    msgBox.exec();

    if (true == shutdown)
	{
        exit(0); // exit application
    }
}

void QeoWebViewJavascriptInterface::notify(QString event, QString options) 
{
}

void QeoWebViewJavascriptInterface::onQeoMsgReceived(QString userId, QString message)
{
	QString javascript = QString("if (window.onQeoMsgReceived) { window.onQeoMsgReceived('%1','%2'); }").arg(userId).arg(message);
	emit invokeJavaScriptCall(javascript);
}


