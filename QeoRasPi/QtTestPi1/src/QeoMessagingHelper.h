#ifndef QEOMSGHELPER_H
#define QEOMSGHELPER_H

#include <QObject>
#include <qeo/api.h>
#include <qeo/types.h>

class QeoMessagingHelper : public QObject
{
	Q_OBJECT

public:
	QeoMessagingHelper();
	void StartListening();
	void StopListening();
	void SendMessage(const char* message);

signals:
	void qeoMsgReceived(QString userId, QString message);

public slots:
	void doWork();

private:
	void onMessage(const char* from, const char* message);
	static void onMessageStatic(const qeo_event_reader_t *reader, const void *data, uintptr_t userdata);

private:
	bool m_Listening;
	qeo_factory_t* m_qeoFactory;
	qeo_event_writer_t* m_msg_writer;
	qeo_event_reader_t* m_msg_reader;
};


#endif // QEOMSGHELPER_H
