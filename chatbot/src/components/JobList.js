import React, { Component } from 'react';
import JobListTable from './JobListTable';
import JobForm from './JobForm';


class JobList extends Component {
   state = {id:'',title:''}

   onSelectJob = (job)=>{
     this.setState({id:job.id,title:job.title});
   }
   render(){
     return(
       <div className="ui container grid">
          <div className="ui row">
            <div className="column two wide">
               <div className="bot-logo">Bot</div>
            </div>
            <div className="column eight wide">
               <div className = "ui input"><input style={{width:400}} value={this.props.message}/></div>
            </div>
          </div>
          <div className="ui row" >
            <div className="column two wide"></div>
            <div className="column seven wide" style={{height:200, overflow:'auto'}} >
            <JobListTable jobs={this.props.jobs} onSelectJob = {this.onSelectJob}/>
            </div>
            <div className="column five wide" >
            <JobForm title={this.state.title} id={this.state.id}/>
            </div>
          </div>
      </div>

   );
   }
}


export default JobList;
