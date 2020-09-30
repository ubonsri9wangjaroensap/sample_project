import React, { Component } from 'react';


class JobListTable extends Component {

  renderList(){
    return this.props.jobs.map((job) => {
      return (

        <div className="item" key={job.title}>
           <div className="right floated content">
              <button
              className="ui button primary"
              onClick={() => this.props.onSelectJob(job)}
              >
                Apply
              </button>
           </div>
           <div className="content">{job.title}</div>
        </div>
      );
    });
  }

 render(){

   return <div className="ui divided list" style={{width:400}}>{this.renderList()}</div>;
 }

}

export default JobListTable;
