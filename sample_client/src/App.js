import React from 'react';
import logo from './logo.svg';
import './App.css';
import axios from 'axios';
class App extends React.Component{
  constructor(props){
    super(props);
    this.state = {
      data:null
    };
  }
  componentDidMount(){
    console.log("call componentDidMount");

     axios.get('http://localhost:8080/welcome')
  .then((response) => {
    console.log(response.data);
    console.log(response.status);
    this.setState({data:response.data});
  });

  }
  render(){
  return (
    <div className="App">
       Hello this is React App page!!!
       {this.state.data}
    </div>
  );
}
}

export default App;
