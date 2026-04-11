#!/bin/bash

TO_EMAIL="smith80java@gmail.com"
FROM_EMAIL="smith-m-80@mail.ru"
SUBJECT="$1"
BODY="$2"
ALLURE_URL="$3"

# Формируем HTML-письмо (для красивого отображения)
HTML_BODY="<html>
<body>
<h2>${SUBJECT}</h2>
<p>${BODY}</p>
<br/>
<p>---</p>
<p>Job: ${JOB_NAME}</p>
<p>Build: #${BUILD_NUMBER}</p>
<p>Duration: ${BUILD_DURATION}</p>
<br/>
<p><a href=\"${BUILD_URL}\">View Build</a></p>
<p><a href=\"${BUILD_URL}/allure\">Allure Report</a></p>
</body>
</html>"

curl --url 'smtps://smtp.mail.ru:465' \
     --ssl-reqd \
     --mail-from "$FROM_EMAIL" \
     --mail-rcpt "$TO_EMAIL" \
     --user "$FROM_EMAIL:${MAIL_PASSWORD}" \
     -T <(echo -e "From: $FROM_EMAIL\nTo: $TO_EMAIL\nSubject: $SUBJECT\nContent-Type: text/html; charset=UTF-8\n\n$HTML_BODY")

