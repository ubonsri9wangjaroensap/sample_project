import React, { Component } from 'react';
import BotMessage from './BotMessage';
import SubmitText from './SubmitText';
import TextMessage from './TextMessage';
import FeedBack from './FeedBack';
import JobSearch from './JobSearch';
import JobList from './JobList';
import '../App.css';
import axios from 'axios';

var temp=[];

class App extends Component {
  state = {messages:[<BotMessage message='Hello from Bot. How I can help you ?'/>]}
  onTextSubmit = (msg) =>{
    console.log(msg);
    this.generateHumanMessage(msg);
  }
  onFeedBackSubmit = (msg) => {
    console.log("in app "+msg);
    this.generateBotMessage(msg);
  }
  onJobSearchSubmit = (msg) =>{
    this.generateJobListMessage(msg);
  }
  async sendMessageRequest (msg, type){
    console.log("message sent: "+msg);
    const response=await axios.post('http://localhost:8081/welcome',{
      data:msg,
      type:type
    });
    console.log(response.data);
    if(response.data.type==='feedback'){
       this.generateFeedBackMessage(response.data);
    }else if(response.data.type==='jobSearch'){
       this.generateJobSearchMessage(response.data);
    }else{
    this.generateBotMessage(response.data);
    }
  }
  generateHumanMessage = (msg)=>{
    temp=this.state.messages;
    console.log(temp);
    temp.push(<TextMessage message={msg}/>)
    this.setState({messages:temp});
    this.sendMessageRequest(msg, 'text');
  }

  generateBotMessage =(response)=>{
    console.log(response.message);
    temp=this.state.messages;
    temp.push(<BotMessage message={response.message}/>);
    this.setState({messages:temp});
  }

  generateJobListMessage = (response)=>{
    console.log(response);
    temp=this.state.messages;
    temp.push(<JobList jobs={response.data} message={response.message}/>);
    this.setState({messages:temp});
  }

  generateFeedBackMessage =(response)=>{
    console.log(response.choices);
    temp=this.state.messages;
    temp.push(<FeedBack choices={response.choices} onFeedBackSubmit={this.onFeedBackSubmit} message={response.message}/>);
    this.setState({messages:temp});
  }
  generateJobSearchMessage =(response)=>{
    temp=this.state.messages;
    temp.push(<JobSearch message={response.message} onJobSearchSubmit={this.onJobSearchSubmit}
      onStartSubmitSearch={this.onStartSubmitSearch}/>);
    this.setState({messages:temp});
  }
  render(){
     return (
         <div>
         <div className="ui container" style={{marginBottom:20}}>
            <SubmitText onSubmit={this.onTextSubmit}/>
         </div>
         <div style={{height:500, overflow:'auto'}}>
          {this.state.messages}
        </div>
        </div>
     );
  }
}

export default App;