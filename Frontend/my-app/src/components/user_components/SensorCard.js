import React from 'react';
import './SensorCard.css';

const SensorCard = ({ type, value }) => {
  return (
    <div className="sensor-card">
      <h4>{type}</h4>
      <p>{value}</p>
    </div>
  );
};

export default SensorCard;
