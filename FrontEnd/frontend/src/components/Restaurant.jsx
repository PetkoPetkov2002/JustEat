import './Restaurant.css';

const Restaurant = ({ restaurant }) => {
  return (
    <div className="restaurant-card">
      <h2 className="restaurant-name">{restaurant.name}</h2>
      <div className="restaurant-info">
        <p className="restaurant-rating">Rating: {restaurant.rating} ⭐</p>
        <div className="restaurant-address">
          <p>{restaurant.address}</p>
        </div>
        <div className="restaurant-cuisines">
          <p>Cuisines: {restaurant.cuisines.join(', ')}</p>
        </div>
      </div>
    </div>
  );
};

export default Restaurant; 