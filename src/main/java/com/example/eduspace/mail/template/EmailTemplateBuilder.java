package com.example.eduspace.mail.template;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public final class EmailTemplateBuilder {

    public static String verificationOtpTemplate(String name, String otp) {

        return """
                <!DOCTYPE html>
                <html>
                <body style="font-family:Arial,sans-serif;background:#f5f5f5;padding:40px;">

                <div style="
                        max-width:600px;
                        margin:auto;
                        background:white;
                        border-radius:12px;
                        padding:40px;
                        box-shadow:0 2px 12px rgba(0,0,0,.08);">

                    <h2 style="color:#d32f2f;">
                        Welcome to EduSpace
                    </h2>

                    <p>
                        Hello <b>%s</b>,
                    </p>

                    <p>
                        Thank you for registering.
                    </p>

                    <p>
                        Your verification code is:
                    </p>

                    <div style="
                            font-size:34px;
                            font-weight:bold;
                            letter-spacing:8px;
                            text-align:center;
                            margin:30px 0;
                            color:#d32f2f;">

                        %s

                    </div>

                    <p>
                        This OTP will expire in
                        <b>10 minutes</b>.
                    </p>

                    <hr>

                    <p style="color:gray;font-size:12px;">
                        If you didn't create this account,
                        please ignore this email.
                    </p>

                </div>

                </body>
                </html>
                """.formatted(name, otp);

    }
}