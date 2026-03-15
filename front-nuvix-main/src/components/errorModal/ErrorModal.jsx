import React from 'react';
import { motion } from 'framer-motion';
import './ErrorModal.css';

export const ErrorModal = ({ message, onClose }) => {
    if (!message) return null;

    return (
        <motion.div
            className="error-modal-overlay"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
        >
            <motion.div
                className="error-modal-content"
                initial={{ scale: 0.5, opacity: 0 }}
                animate={{ scale: 1, opacity: 1 }}
                exit={{ scale: 0.5, opacity: 0 }}
            >
                <h3>Error</h3>
                <p>{message}</p>
                <button onClick={onClose} className="btn btn-error">
                    Cerrar
                </button>
            </motion.div>
        </motion.div>
    );
};