import React, { useState, useEffect } from 'react';
import DataTable from '../common_components/DataTable';
import Pagination from '../common_components/Pagination';
import UserDetail from './UserDetail';
import './UsersSection.css';

function UsersSection() {
  const [users, setUsers] = useState([]);
  const [selectedUser, setSelectedUser] = useState(null);
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize] = useState(10);
  const [totalPages, setTotalPages] = useState(1);
  const [searchTerm, setSearchTerm] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchUsers(currentPage);
  }, [currentPage]);

  const fetchUsers = async (page) => {
    setLoading(true);
    try {
      const response = await fetch(`https://localhost:9001/api/v1/AdminPanel/users/get-all-users?pageNumber=${page}&pageSize=${pageSize}`, {
        headers: { Authorization: `Bearer ${sessionStorage.getItem('token')}` }
      });
      
      if (!response.ok) {
        throw new Error(`Failed to fetch users: ${response.status}`);
      }
      
      const data = await response.json();
      
      setUsers(data.users || []);
      setTotalPages(data.totalPages || 1);
      setCurrentPage(data.currentPage || 1);
      setError(null);
    } catch (error) {
      console.error('User data fetch error:', error);
      setError('Failed to load users. Please try again later.');
      setUsers([]);
    } finally {
      setLoading(false);
    }
  };

  const handleUserClick = (user) => {
    setSelectedUser(user);
  };

  const handlePageChange = (newPage) => {
    if (newPage >= 1 && newPage <= totalPages) {
      setCurrentPage(newPage);
    }
  };

  const handleAddUser = () => {
    console.log('Add user clicked');
  };

  const handleEditUser = (userId, event) => {
    if (event) event.stopPropagation();
    const userToEdit = users.find(user => user.id === userId);
    if (userToEdit) {
      setSelectedUser(userToEdit);
    }
  };

  const handleDeleteUser = (userId, event) => {
    if (event) event.stopPropagation();
    if (window.confirm('Are you sure you want to delete this user?')) {
      console.log('Delete user:', userId);
    }
  };

  const filteredUsers = users.filter(user => {
    if (!searchTerm.trim()) return true;
    
    const searchTermLower = searchTerm.toLowerCase();
    return (
      (user.id && user.id.toLowerCase().includes(searchTermLower)) ||
      (user.userName && user.userName.toLowerCase().includes(searchTermLower)) ||
      (user.email && user.email.toLowerCase().includes(searchTermLower)) ||
      (user.firstName && user.firstName.toLowerCase().includes(searchTermLower)) ||
      (user.lastName && user.lastName.toLowerCase().includes(searchTermLower)) ||
      (user.roles && user.roles.some(role => role.toLowerCase().includes(searchTermLower)))
    );
  });

  const columns = [
    { 
      key: 'name', 
      header: 'Name',
      render: (user) => (
        <div className="user-name-cell">
          <div className="user-avatar-sm">
            <span>{user.firstName?.charAt(0) || ''}{user.lastName?.charAt(0) || ''}</span>
          </div>
          <div className="user-info">
            <div className="user-full-name">{user.firstName} {user.lastName}</div>
            <div className="user-username">@{user.userName}</div>
          </div>
        </div>
      )
    },
    { key: 'email', header: 'Email' },
    { 
      key: 'roles', 
      header: 'Roles',
      render: (user) => (
        <div className="role-tags">
          {user.roles && user.roles.map((role, index) => (
            <span key={index} className="role-tag">{role}</span>
          ))}
        </div>
      )
    },
    { 
      key: 'status', 
      header: 'Status',
      render: () => <span className="status-badge available">Active</span>
    },
    { 
      key: 'actions', 
      header: 'Actions',
      className: 'actions-column',
      render: (user) => (
        <div className="cell actions">
          <button 
            className="action-button"
            onClick={(e) => handleEditUser(user.id, e)}
            title="Edit User"
          >
            <i className="fas fa-edit"></i>
          </button>
          <button 
            className="action-button"
            onClick={(e) => handleDeleteUser(user.id, e)}
            title="Delete User"
          >
            <i className="fas fa-trash"></i>
          </button>
        </div>
      )
    }
  ];

  if (loading && !filteredUsers.length) {
    return <div className="loading-spinner"><i className="fas fa-spinner fa-spin"></i> Loading users...</div>;
  }

  return (
    <div className="users-management">
      <h2>Users Management</h2>
      
      {selectedUser ? (
        <UserDetail 
          user={selectedUser} 
          onBack={() => setSelectedUser(null)} 
        />
      ) : (
        <>
          <div className="list-header">
            <h3>Users List</h3>
            <div className="list-controls">
              <div className="search-wrapper">
                <i className="fas fa-search search-icon"></i>
                <input 
                  type="text" 
                  placeholder="Search users..." 
                  className="search-input"
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                />
                {searchTerm && (
                  <button 
                    className="clear-search" 
                    onClick={() => setSearchTerm('')}
                    title="Clear search"
                  >
                    <i className="fas fa-times"></i>
                  </button>
                )}
              </div>
            </div>
          </div>
          
          {error && (
            <div className="error-message">
              <i className="fas fa-exclamation-triangle"></i>
              {error}
            </div>
          )}
          
          <DataTable
            columns={columns}
            data={filteredUsers}
            isLoading={loading}
            loadingText="Loading users..."
            emptyText="No users found"
            onRowClick={handleUserClick}
            keyField="id"
            className="users-table"
          />
          
          {filteredUsers.length > 0 && (
            <Pagination
              currentPage={currentPage}
              totalPages={totalPages}
              onPageChange={handlePageChange}
              className="users-pagination"
            />
          )}
        </>
      )}
    </div>
  );
}

export default UsersSection;