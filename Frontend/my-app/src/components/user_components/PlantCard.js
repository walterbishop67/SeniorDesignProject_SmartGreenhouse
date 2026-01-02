import React from 'react';
import './PlantCard.css';

const PlantCard = ({ name }) => {
  return (
    <div className="plant-card">
      <div className="plant-image">🌱</div>
      <p>{name}</p>
    </div>
  );
};

export default PlantCard;
