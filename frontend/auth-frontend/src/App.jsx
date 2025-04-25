import { useState } from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from '/vite.svg'
import './App.css'
import { useEffect } from 'react'

function App() {
  const [count, setCount] = useState(0)

  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [message, setMessage] = useState('')
  const [jwt, setJwt] = useState('')

  const [profile, setProile] = useState({
    username: '',
    password: '',
    role: []
  })

  const handleSubmit = async (e) => {

    e.preventDefault()

    const data = {
      username: username,
      password: password
    }
    const response = await fetch('http://localhost:8080/signin', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer '
      },
      body: JSON.stringify(data)
    })
    if (response.ok) {
      const responseData = await response.json()
      const currentDate = new Date();
      const formattedDate = currentDate.toLocaleString();
      setMessage("Date: " + formattedDate + " :: " + "username::" + responseData.username)

      localStorage.setItem('token', responseData.jwtToken)
      setJwt(responseData.jwtToken);
    }
    else {
      const errorData = await response.json()
      setError(errorData.message)
    }

  }
  const onProfileClick = async () => {
    const response = await fetch('http://localhost:8080/profile', {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${jwt}`
      }
    })
    if (response.ok) {
      const responseData = await response.json()
      setProile({
        username: responseData.username,
        password: responseData.password,
        role: responseData.roles
      })

    }
  }

  useEffect(() => {
    const token = localStorage.getItem('token')
    if (token) {
      setJwt(token)
    }
  }, [  ])

  const onLogoutClick = async () => {
    const token = localStorage.getItem('token')
    if (token) {
      
      localStorage.removeItem('token')
      setJwt('')
      setProile({
        username: '',
        password: '',
        role: []
      })
      fetch('http://localhost:8080/logout', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        }
      }).then(res=>{
      
          localStorage.removeItem('token')
          setJwt('')
          setProile({
            username: '',
            password: '',
            role: []
          })
      }).catch(err=>{
        console.log("err occurred while logging out", err)
      })
    }

  }
  return (
    <>

      <h1>Auth Applciation with spring boot </h1>


      {jwt ? (
        <div>
          <button onClick={onProfileClick}>Get your details</button>

          <div style={{
            backgroundColor: '#242424',
            padding: '20px',
            borderRadius: '5px',
            marginTop: '10px',
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center'
          }}>

            <ul >
              <li>Username: {profile.username}</li>

              <li>Role: {profile.role.map((role, index) => <span key={index} style={{ fontWeight: 800, color: 'green' }}>{role}</span>)}</li>
            </ul>
            <button style={{
              backgroundColor: 'red',
              color: 'white',
              border: 'none',
              padding: '10px 20px',
              cursor: 'pointer'
            }}
              onClick={onLogoutClick}
            >Logout </button>
          </div>

        </div>
      ) :
        <div className="card">
          <form onSubmit={handleSubmit}>

            <div>
              <label htmlFor="username">Username</label>
              <input type="text" id="username" value={username} onChange={(e) => setUsername(e.target.value)} />
            </div>

            <div>
              <label htmlFor="username">Username:</label>
              <input type="password" id="username" value={password} onChange={(e) => setPassword(e.target.value)} />
            </div>

            <div>
              <button type="submit">Login</button>
            </div>

            <div>
              {error && <div style={{ color: 'red' }}>{error}</div>}
              {/* <div style={{ color: 'green' }}>{message || "Not available"}</div>
          {{}} */}
              {message && <div style={{ color: 'green' }}>{message}</div>}


              {jwt && <div>Your jwt token:: {jwt}</div>}
            </div>


          </form>
        </div>
      }


    </>
  )
}

export default App
