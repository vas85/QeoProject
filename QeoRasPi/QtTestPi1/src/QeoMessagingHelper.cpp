#define FACTORY_H_ // hack to not include "factory.h" as it has incorrect linkage
#include "QeoMessagingHelper.h"
#include <pwd.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/types.h>

extern "C" qeo_factory_t *qeo_factory_create();
extern "C" void qeo_factory_close(qeo_factory_t *factory);

typedef struct {
	char * from;
	char * message;
} org_qeo_qeomessaging_Message_t;

const DDS_TypeSupport_meta org_qeo_qeomessaging_Message_type[] = {
	{
		/* .tc = */ CDR_TYPECODE_STRUCT,
		/* .flags = */ (tsm_flags)(TSMFLAG_DYNAMIC | TSMFLAG_GENID | TSMFLAG_MUTABLE),
		/* .name = */ "org.qeo.qeomessaging.ChatMessage",
		/* .size = */ sizeof(org_qeo_qeomessaging_Message_t),
		/* .offset = */ 0,
		/* .nelem = */ 2,
		/* .label = */ 0,
		/* .tsm = */ 0
	},
	{
		/* .tc = */ CDR_TYPECODE_CSTRING,
		/* .flags = */ TSMFLAG_DYNAMIC,
		/* .name = */ "from",
		/* .size = */ 0,
		/* .offset = */ offsetof(org_qeo_qeomessaging_Message_t, from),
		/* .nelem = */ 0,
		/* .label = */ 0,
		/* .tsm = */ 0
	},
	{
		/* .tc = */ CDR_TYPECODE_CSTRING,
		/* .flags = */ TSMFLAG_DYNAMIC,
		/* .name = */ "message",
		/* .size = */ 0,
		/* .offset = */ offsetof(org_qeo_qeomessaging_Message_t, message),
		/* .nelem = */ 0,
		/* .label = */ 0,
		/* .tsm = */ 0
	},
};

static char *default_user(void)
{
	struct passwd *pwd = getpwuid(getuid());
	char *name = "";

	if (NULL != pwd) {
		name = strdup(pwd->pw_name);
	}
	return name;
}

QeoMessagingHelper::QeoMessagingHelper() : m_Listening(false), m_qeoFactory(0), m_msg_reader(0), m_msg_writer(0)
{

}

void QeoMessagingHelper::StartListening()
{
	if (!m_Listening)
	{
		m_qeoFactory = qeo_factory_create();
		if (m_qeoFactory != NULL)
		{
			static qeo_event_reader_listener_t _listener = { &QeoMessagingHelper::onMessageStatic, // on_data
				0, // on_no_more_data
				0 // on_policy_update
			};

			m_msg_writer = qeo_factory_create_event_writer(m_qeoFactory, org_qeo_qeomessaging_Message_type, NULL, 0);
			m_msg_reader = qeo_factory_create_event_reader(m_qeoFactory, org_qeo_qeomessaging_Message_type, &_listener, (uintptr_t)this);
			m_Listening = true;

			printf("Qeo Initialized.\n");
		}
		//QMetaObject::invokeMethod(this, "doWork", Qt::QueuedConnection);
	}
}

void QeoMessagingHelper::StopListening()
{
	if (m_Listening)
	{
		if (m_msg_reader)
		{
			qeo_event_reader_close(m_msg_reader);
		}

		if (m_msg_writer)
		{
			qeo_event_writer_close(m_msg_writer);
		}

		if (m_qeoFactory)
		{
			qeo_factory_close(m_qeoFactory);
		}
	}
}

void QeoMessagingHelper::SendMessage(const char* message)
{
	char buf[128];
	org_qeo_qeomessaging_Message_t chat_msg;

	if (m_Listening && m_msg_writer)
	{
		strncpy(buf, message, sizeof(buf));
		chat_msg.from = default_user();
		chat_msg.message = buf;
		qeo_event_writer_write(m_msg_writer, &chat_msg);

		free(chat_msg.from);
	}
}

void QeoMessagingHelper::doWork()
{
}

void QeoMessagingHelper::onMessage(const char* from, const char* message)
{
	printf("%s : %s\n", from, message);
	emit qeoMsgReceived(QString(from), QString(message));
}

void QeoMessagingHelper::onMessageStatic(const qeo_event_reader_t *reader, const void *data, uintptr_t userdata)
{
	if (userdata)
	{
		org_qeo_qeomessaging_Message_t *msg = (org_qeo_qeomessaging_Message_t *)data;
		((QeoMessagingHelper*)userdata)->onMessage(msg->from, msg->message);
	}
}
