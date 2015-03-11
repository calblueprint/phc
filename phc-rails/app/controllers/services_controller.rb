class ServicesController < ApplicationController
  def show
      render :json => Service.services
  end
end
