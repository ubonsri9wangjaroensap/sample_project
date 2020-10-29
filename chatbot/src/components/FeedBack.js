import React, { Component } from 'react';
import axios from 'axios';
import '../App.css';

class FeedBack extends Component {

  handleOnClick = (value)=>{
    console.log("value: "+value);
    this.sendFeedbackRequest(value,'FEEDBACK');
  }

  async sendFeedbackRequest (msg, type){
    const response=await axios.post('http://localhost:9000/talk',{
      data:msg,
      type:type
    });
    this.props.onFeedBackSubmit(response.data);
  }

  render(){
    return (

      <div className="ui container grid">
         <div className="ui row">
         <div className="column two wide">
         <div className="bot-logo">Bot</div>
         </div>
         <div className="column eight wide">
         <div className = "ui input"><input style={{width:400}} value={this.props.message}/>
          </div>
         </div>
         </div>
         <div className="ui row">
         <div className="column two wide"></div>
      {this.props.choices.map((item, index) => (

    <div className="column two wide">
            <button class="ui button" onClick={(e)=>this.handleOnClick(item)}>{item}</button>

            </div>
      ))}
      </div>
      </div>
    );
  }


}

export default FeedBack;
