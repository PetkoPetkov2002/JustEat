import { useState } from 'react';
import axios from 'axios';
import Restaurant from './Restaurant';
import './SearchPage.css';

const SearchPage = () => {
  const [searchQuery, setSearchQuery] = useState('');
  const [restaurants, setRestaurants] = useState([]);
  const [error, setError] = useState('');

  const handleSearch = async () => {
    try {
      setError('');
      const response = await axios.get(`/api/restaurants/${encodeURIComponent(searchQuery)}`);
      setRestaurants(response.data);
    } catch (error) {
      console.error('Error during search:', error);
      // Backend sends error message directly as string
      setError(error.response?.data || 'An unexpected error occurred');
      setRestaurants([]);
    }
  };

  return (
    <div className="search-container">
      <div className="search-box">
        <input
          type="text"
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          placeholder="Enter postcode..."
          className="search-input"
        />
        <button onClick={handleSearch} className="search-button">
          Search
        </button>
      </div>
      
      {error && <div className="error-message">{error}</div>}
      
      <div className="restaurants-list">
        {restaurants.map((restaurant, index) => (
          <Restaurant key={index} restaurant={restaurant} />
        ))}
      </div>
    </div>
  );
};

export default SearchPage; 