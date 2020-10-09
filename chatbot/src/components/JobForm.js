import React, { Component } from 'react';
import axios from 'axios';

class JobForm extends Component {

  state={username:'',password:'',result:''};
  handleOnClick=(e)=>{
    let data ={username:this.state.username,password:this.state.password,jobReqId:this.props.id};
    console.log(data);
    this.sendApplicationRequest(data,"jobApplication");
  }
  async sendApplicationRequest (data,type){
    console.log("message sent: "+data);
    const response=await axios.post('http://localhost:8081/welcome',{
      data:data,
      type:type
    });

    this.setState({result:response.data.message});
  }
     render(){
       if(!this.props.title){
         return <div></div>
       }
       return (
         <div style={{marginLeft:0}}>
    <p>
    Title: {this.props.title}
    <br/>
    Job Req Id: {this.props.id}
    </p>
    <div className = "ui input"><input style={{width:120}} placeholder="username"
         onChange={(e) =>this.setState({firstName:e.target.value})}/>&nbsp;&nbsp;</div>
    <div className = "ui input"><input style={{width:120}} placeholder="password"
         onChange={(e) =>this.setState({lastName:e.target.value})}/></div>
    <br/><br/>
    <button className="ui button" style={{width:120}} onClick={this.handleOnClick}>
      Submit
    </button>
    <div style={{display: this.state.result!=='' ? 'inline' : 'none', width:120,
      color:this.state.result==='Success'?'green':'red', fontSize:20}}>
      &nbsp;&nbsp;&nbsp;&nbsp;<b>{this.state.result}</b>
    </div>
    </div>
      );

     }



}

export default JobForm;
