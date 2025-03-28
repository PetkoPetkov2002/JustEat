import { useState } from 'react';
import axios from 'axios';
import './SearchPage.css';

const SearchPage = () => {
  const [searchQuery, setSearchQuery] = useState('');

  const handleSearch = async () => {
    try {
      const response = await axios.post('http://localhost:3000/api/search', {
        query: searchQuery
      });
      console.log(response.data);
      // Handle the response data here
    } catch (error) {
      console.error('Error during search:', error);
    }
  };

  return (
    <div className="search-container">
      <div className="search-box">
        <input
          type="text"
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          placeholder="Search for restaurants..."
          className="search-input"
        />
        <button onClick={handleSearch} className="search-button">
          Search
        </button>
      </div>
    </div>
  );
};

export default SearchPage; 