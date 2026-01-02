import React from 'react';
import './ConfirmModal.css';

const ConfirmModal = ({ open, title, message, onConfirm, onCancel, confirmText = "Evet", cancelText = "İptal", hideButtons = false }) => {
  if (!open) return null;
  return (
    <div className="confirm-modal-overlay">
      <div className="confirm-modal">
        <h3>{title}</h3>
        <p>{message}</p>
        {!hideButtons && (
          <div className="modal-buttons">
            <button className="confirm-btn" onClick={onConfirm}>{confirmText}</button>
            <button className="cancel-btn" onClick={onCancel}>{cancelText}</button>
          </div>
        )}
      </div>
    </div>
  );
};

export default ConfirmModal; 