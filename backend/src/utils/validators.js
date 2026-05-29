const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

class ValidationError extends Error {
  constructor(message) {
    super(message);
    this.name = 'ValidationError';
    this.statusCode = 400;
  }
}

const validateEmail = (email) => {
  if (!email || typeof email !== 'string') {
    throw new ValidationError('Email is required');
  }
  const normalized = email.trim().toLowerCase();
  if (!EMAIL_REGEX.test(normalized)) {
    throw new ValidationError('Invalid email format');
  }
  return normalized;
};

const validatePassword = (password) => {
  if (!password || typeof password !== 'string') {
    throw new ValidationError('Password is required');
  }
  if (password.length < 8) {
    throw new ValidationError('Password must be at least 8 characters');
  }
  if (!/[A-Za-z]/.test(password)) {
    throw new ValidationError('Password must contain at least one letter');
  }
  if (!/[0-9]/.test(password)) {
    throw new ValidationError('Password must contain at least one number');
  }
  if (!/[^A-Za-z0-9]/.test(password)) {
    throw new ValidationError('Password must contain at least one special character');
  }
  return password;
};

const validateUsername = (username) => {
  if (!username || typeof username !== 'string') {
    return null;
  }
  const trimmed = username.trim();
  if (trimmed.length === 0) return null;
  if (trimmed.length < 3 || trimmed.length > 30) {
    throw new ValidationError('Username must be 3-30 characters');
  }
  if (!/^[a-zA-Z0-9_]+$/.test(trimmed)) {
    throw new ValidationError('Username can only contain letters, numbers, and underscores');
  }
  return trimmed;
};

const validatePasswordsMatch = (password, confirmPassword) => {
  if (password !== confirmPassword) {
    throw new ValidationError('Passwords do not match');
  }
};

module.exports = {
  validateEmail,
  validatePassword,
  validateUsername,
  validatePasswordsMatch,
  ValidationError,
};
