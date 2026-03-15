import React from 'react';
import { motion } from 'framer-motion';
import './ParticipantModal.css';

export const ParticipantModal = ({ participant, estadoEntrada, onClose }) => {
    if (!participant) return null;

    return (
        <motion.div
            className="modal-overlay"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
        >
            <motion.div
                className="modal-content"
                initial={{ y: -50, opacity: 0 }}
                animate={{ y: 0, opacity: 1 }}
                exit={{ y: -50, opacity: 0 }}
            >
                <h3>Participante Verificado</h3>
                <p><strong>Nombre:</strong> {participant.nombre} {participant.apellido}</p>
                <p><strong>DNI:</strong> {participant.dni}</p>
                <p><strong>Email:</strong> {participant.email}</p>
                <p><strong>Estado:</strong> {estadoEntrada}</p>
                <button onClick={onClose} className="btn">
                    Cerrar
                </button>
            </motion.div>
        </motion.div>
    );
};