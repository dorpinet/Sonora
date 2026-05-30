const errorMiddleware = (err, req, res, next) => {
  console.error('Error:', err);


  if (err.name === 'ValidationError') {
    if (err.errors) {
      const messages = Object.values(err.errors).map(val => val.message);
      return res.status(400).json({ error: 'Validation Error', messages });
    }
    return res.status(err.statusCode || 400).json({ error: err.message || 'Validation failed' });
  }


  if (err.code === 11000) {
    const field = Object.keys(err.keyValue)[0];
    return res.status(409).json({ error: `${field} already exists.` });
  }


  if (err.name === 'CastError') {
    return res.status(400).json({ error: `Invalid ${err.path}: ${err.value}` });
  }


  if (err.name === 'JsonWebTokenError') {
    return res.status(401).json({ error: 'Invalid token.' });
  }
  if (err.name === 'TokenExpiredError') {
    return res.status(401).json({ error: 'Token expired.' });
  }


  const statusCode = err.statusCode || 500;
  const message = err.message || 'Internal Server Error';

  res.status(statusCode).json({ error: message });
};

module.exports = errorMiddleware;
