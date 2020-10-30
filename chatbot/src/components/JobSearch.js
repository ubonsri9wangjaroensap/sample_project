import React, { Component } from 'react';
import axios from 'axios';
import '../App.css';


class JobSearch extends Component {
  state = {title:'',
           city:'',
           state:'',
           country:''
          };

  onCountrySelected = (e) =>{
    console.log(e.target.value);
    this.setState({country:e.target.value});
  }
  onStateSelected = (e)=>{
    console.log(e.target.value);
    this.setState({state:e.target.value});
  }
  handleOnClick=(e)=>{
    let data ={title:this.state.title,city:this.state.city,state:this.state.state,country:this.state.country};
    console.log(data);
    this.props.onWaiting(true);
    this.sendSearchRequest(data,"JOB_LIST");
  }
  async sendSearchRequest (data,type){
    console.log("message sent: "+data);
    const response=await axios.post('http://localhost:9000/talk/jobReqSearch',{
      data:data,
      type:type
    });
    this.props.onWaiting(false);
    this.props.onJobSearchSubmit(response.data);
  }
  render(){

    return (
      <div className="ui container grid">
         <div className="ui row">
           <div className="column two wide">
              <div className="bot-logo">Bot</div>
           </div>
           <div className="column eight wide">
              <div className = "ui input"><input style={{width:400}} value={this.props.message}/></div>
           </div>
         </div>
         <div className="ui row">
           <div className="column two wide"></div>
           <div className="column four wide">
               <div className = "ui input"><input style={{width:330}} placeholder="Job Title"
               onChange={(e) =>this.setState({title:e.target.value})}/></div>
           </div>
        </div>
        <div className="ui row">
           <div className="column two wide"></div>
           <div className="column three wide">
               <div className = "ui input"><input style={{width:150}} placeholder="City"
               onChange={(e) =>this.setState({city:e.target.value})}/></div>
           </div>
           <div className="column three wide">
            <select class="ui dropdown" style={{width:150}}
              value={this.state.state}
              onChange={this.onStateSelected}
            >
              <option value="">State</option>
              <option value="CA">California</option>
              <option value="IL">Illinois</option>
            </select>
           </div>
       </div>
       <div className="ui row">
           <div className="column two wide"></div>
           <div className="column three wide">
             <select class="ui dropdown" style={{width:150}}
               value={this.state.country}
               onChange={this.onCountrySelected}
             >
               <option value="">Country</option>
               <option value="US">United States</option>
               <option value="TH">Thailand</option>
             </select>
           </div>
           <div className="column three wide">
               <button class="ui button" style={{width:150}} onClick={this.handleOnClick}>Submit</button>
           </div>
        </div>
      </div>
    );
  }


}

export default JobSearch;
