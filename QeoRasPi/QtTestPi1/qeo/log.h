/************** COPYRIGHT AND CONFIDENTIALITY INFORMATION *********************
 **                                                                          **
 ** Copyright (c) 2013 Technicolor                                           **
 ** All Rights Reserved                                                      **
 **                                                                          **
 ** This program contains proprietary information which is a trade           **
 ** secret of TECHNICOLOR and/or its affiliates and also is protected as     **
 ** an unpublished work under applicable Copyright laws. Recipient is        **
 ** to retain this program in confidence and is not permitted to use or      **
 ** make copies thereof other than as permitted in a written agreement       **
 ** with TECHNICOLOR, UNLESS OTHERWISE EXPRESSLY ALLOWED BY APPLICABLE LAWS. **
 **                                                                          **
 ******************************************************************************/

/** \file
 * Qeo logging API.
 */

#ifndef QEO_LOG_H_
#define QEO_LOG_H_
#ifdef OBJC
#import <Foundation/Foundation.h>
#endif

/**
 * Qeo log levels.
 */
typedef enum {
    QEO_LOG_DEBUG,                     /**< for debug log entries (only if
                                            compiled with DEBUG=1) */
    QEO_LOG_INFO,                      /**< for informational log entries (only
                                            if compiled with DEBUG=1) */
    QEO_LOG_WARNING,                   /**< for warning log entries */
    QEO_LOG_ERROR,                     /**< for error log entries */
} qeo_loglvl_t;

/**
 * Prototype of the Qeo log function with printf style arguments.
 *
 * \param[in] lvl the log level
 * \param[in] format the message format (extra arguments may follow)
 */
typedef void  (*qeo_logger_ptr)(qeo_loglvl_t lvl, const char* fileName, const char* functionName, int lineNumber, const char *format, ...);

void qeo_log_set_logger(qeo_logger_ptr logger);

extern qeo_logger_ptr qeo_logger;

// TODO: add time stamp

#if DEBUG == 1

#ifdef OBJC
#   	define qeo_log_d(format, ...) NSLog(@"D:%s %d %s %s", __FILE__, __LINE__, __PRETTY_FUNCTION__, __FUNCTION__, format, ##__VA_ARGS__);
#   	define qeo_log_i(format, ...) NSLog(@"I:%s %d %s %s", __FILE__, __LINE__, __PRETTY_FUNCTION__, __FUNCTION__, format, ##__VA_ARGS__);
#else
#	define qeo_log_d(format, ...) if (qeo_logger) qeo_logger(QEO_LOG_DEBUG, __FILE__, __func__, __LINE__, format, ##__VA_ARGS__)
#	define qeo_log_i(format, ...) if (qeo_logger) qeo_logger(QEO_LOG_INFO, __FILE__, __func__, __LINE__, format, ##__VA_ARGS__)
#endif

#else
#define qeo_log_d(format, ...)
#define qeo_log_i(format, ...)

#endif

#include <string.h>
#define __FILENAME__ (strrchr(__FILE__, '/') ? strrchr(__FILE__, '/') + 1 : __FILE__)


#ifdef OBJC
#  	define qeo_log_e(format, ...) NSLog(@"E:%s %d %s %s", __FILENAME__, __LINE__, __PRETTY_FUNCTION__, __FUNCTION__, format, ##__VA_ARGS__);
#   	define qeo_log_w(format, ...) NSLog(@"W:%s %d %s %s", __FILENAME__, __LINE__, __PRETTY_FUNCTION__, __FUNCTION__, format, ##__VA_ARGS__);
#else
#	define qeo_log_w(format, ...) if (qeo_logger) qeo_logger(QEO_LOG_WARNING, __FILENAME__, __func__, __LINE__, format, ##__VA_ARGS__)
#	define qeo_log_e(format, ...) if (qeo_logger) qeo_logger(QEO_LOG_ERROR, __FILENAME__, __func__, __LINE__, format, ##__VA_ARGS__)
#endif

#endif /* QEO_LOG_H_ */
