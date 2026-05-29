const sgMail = require('@sendgrid/mail');

sgMail.setApiKey(process.env.SENDGRID_API_KEY);

class EmailService {
  static generateOtp() {
    return Math.floor(100000 + Math.random() * 900000).toString();
  }

  static async sendOtp(email, code, purpose = 'register') {
    const purposeText = {
      register: 'SONORA Registration Verification',
      reset_password: 'SONORA Password Reset',
      change_email: 'SONORA Email Change',
    };

    const msg = {
      to: email,
      from: process.env.SENDGRID_FROM_EMAIL,
      subject: purposeText[purpose] || 'SONORA Verification Code',
      text: `Your verification code: ${code}\n\nValid for 10 minutes. Do not share it with anyone.`,
      html: `
        <div style="font-family: Arial, sans-serif; max-width: 400px; margin: 0 auto; padding: 20px;">
          <h2 style="color: #1a1a1a;">SONORA</h2>
          <p>Your verification code:</p>
          <div style="font-size: 32px; font-weight: bold; letter-spacing: 4px; padding: 15px; background: #f0f0f0; border-radius: 8px; text-align: center; margin: 20px 0;">
            ${code}
          </div>
          <p style="color: #666; font-size: 14px;">Valid for 10 minutes. Do not share it with anyone.</p>
        </div>
      `,
    };

    await sgMail.send(msg);
  }

  static async sendWelcomeEmail(email, username) {
    const msg = {
      to: email,
      from: process.env.SENDGRID_FROM_EMAIL,
      subject: 'Welcome to SONORA!',
      text: `Hi ${username || ''}! Welcome to SONORA — your music space.`,
      html: `
        <div style="font-family: Arial, sans-serif; max-width: 400px; margin: 0 auto; padding: 20px;">
          <h2 style="color: #1a1a1a;">SONORA</h2>
          <p>Hi, <strong>${username || ''}</strong>!</p>
          <p>Welcome to SONORA — your music space.</p>
        </div>
      `,
    };

    await sgMail.send(msg);
  }
}

module.exports = EmailService;
